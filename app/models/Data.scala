package models

case class Data(base: String, currency: String, rollingAvg: Double, prices: Array[Price]) extends Serializable
