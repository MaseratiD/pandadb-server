package cn.pandadb.driver.values

import java.time.temporal.ChronoUnit.{DAYS, MONTHS, NANOS, SECONDS}
import java.time.temporal.{Temporal, TemporalAmount, TemporalUnit, UnsupportedTemporalTypeException}
import java.util
import java.util.Arrays.asList
import java.util.Collections.unmodifiableList
import java.util.Objects

trait IsoDuration extends TemporalAmount with Serializable {
  def months(): Long

  def days(): Long

  def seconds(): Long

  def nanoseconds(): Long
}

class Duration(months: Long, days: Long, seconds: Long, nanoseconds: Long) extends IsoDuration with Serializable {
  val NANOS_PER_SECOND = 1000000000
  val SUPPORTED_UNITS: util.List[TemporalUnit] = unmodifiableList(asList(MONTHS, DAYS, SECONDS, NANOS))

  override def months(): Long = months

  override def days(): Long = days

  override def seconds(): Long = seconds

  override def nanoseconds(): Long = nanoseconds

  override def get(unit: TemporalUnit): Long = {
    if (unit eq MONTHS)  months
    else if (unit eq DAYS)  days
    else if (unit eq SECONDS)  seconds
    else if (unit eq NANOS)  nanoseconds
    else throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit)
  }

  override def getUnits: util.List[TemporalUnit] = SUPPORTED_UNITS

  override def addTo(temp: Temporal): Temporal = {
    var temporal = temp
    if (months != 0) temporal = temporal.plus(months, MONTHS)
    if (days != 0) temporal = temporal.plus(days, DAYS)
    if (seconds != 0) temporal = temporal.plus(seconds, SECONDS)
    if (nanoseconds != 0) temporal = temporal.plus(nanoseconds, NANOS)
    temporal
  }

  override def subtractFrom(temp: Temporal): Temporal = {
    var temporal = temp
    if (months != 0) temporal = temporal.minus(months, MONTHS)
    if (days != 0) temporal = temporal.minus(days, DAYS)
    if (seconds != 0) temporal = temporal.minus(seconds, SECONDS)
    if (nanoseconds != 0) temporal = temporal.minus(nanoseconds, NANOS)
    temporal
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append('P')
    sb.append(months).append('M')
    sb.append(days).append('D')
    sb.append('T')
    if (seconds < 0 && nanoseconds > 0) if (seconds == -1) sb.append("-0")
    else sb.append(seconds + 1)
    else sb.append(seconds)
    if (nanoseconds > 0) {
      val pos = sb.length
      if (seconds < 0) sb.append(2 * NANOS_PER_SECOND - nanoseconds)
      else sb.append(NANOS_PER_SECOND + nanoseconds)
      sb.setCharAt(pos, '.')

    }
    sb.append('S')
    sb.toString
  }
}
