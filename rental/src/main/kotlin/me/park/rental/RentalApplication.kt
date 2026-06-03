package me.park.rental

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class RentalApplication

fun main(args: Array<String>) {
    runApplication<RentalApplication>(*args)
}
