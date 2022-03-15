package net.cuodex.passxapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PassxApiApplication

fun main(args: Array<String>) {
	runApplication<PassxApiApplication>(*args)
	println("hi")
}
