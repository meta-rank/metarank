package me.dfdx.metarank.feature

import java.io.{DataInput, DataOutput}

import me.dfdx.metarank.model.Timestamp

case class TumblingWindowCountingFeature(name: String) extends Feature {
  override def write(out: DataOutput): Unit = ???
}

object TumblingWindowCountingFeature extends Feature.Loader[TumblingWindowCountingFeature] {

  case class CircularReservoir(lastDay: Int, size: Int, buffer: Vector[Int]) {
    def sumLast(days: Int): Int = {
      var sum = 0
      var pos = math.max(lastDay - days, 0)
      while (pos < lastDay) {
        sum += buffer(wrap(pos))
        pos += 1
      }
      sum
    }

    def increment(day: Int, value: Int = 1): CircularReservoir = {
      if (lastDay == 0) {
        // initial increment
        val position = wrap(day)
        copy(lastDay = day, buffer = buffer.updated(position, buffer(position) + value))
      } else if (day == lastDay) {
        // same day increment
        val position = wrap(day)
        copy(buffer = buffer.updated(position, buffer(position) + value))
      } else {
        // the day is incremented
        if (day - lastDay >= size) {
          // we have a full loop over circular buffer, so we need to wipe everything
          copy(lastDay = day, buffer = Vector.fill(size)(0).updated(wrap(day), value))
        } else {
          // we're within the buffer size,
          // so we iterate from the next day to the current one, zeroing everything in between
          // in case if single day increment, (lastDay+1) == ts.day, so this loop is skipped
          val skipped = ((lastDay + 1) until day).foldLeft(buffer)((buf, iday) => buf.updated(wrap(iday), 0))
          copy(lastDay = day, buffer = skipped.updated(wrap(day), value))
        }
      }
    }

    def wrap(day: Int) = day % size
  }

  object CircularReservoir {
    def apply(windowSizeDays: Int) = new CircularReservoir(0, windowSizeDays, Vector.fill(windowSizeDays)(0))
  }
  def apply(name: String, windowSizeDays: Int)                    = ???
  override def load(in: DataInput): TumblingWindowCountingFeature = ???
}
