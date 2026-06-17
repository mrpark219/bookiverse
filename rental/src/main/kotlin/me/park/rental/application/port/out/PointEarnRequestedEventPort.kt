package me.park.rental.application.port.out

import me.park.rental.application.event.PointEarnRequestedEvent

interface PointEarnRequestedEventPort {

    fun save(event: PointEarnRequestedEvent)
}
