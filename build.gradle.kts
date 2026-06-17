val serviceBuilds = listOf("gateway", "book-catalog", "book", "rental", "user")

tasks.register("test") {
    group = "verification"
    description = "Runs tests for all service builds."
    dependsOn(serviceBuilds.map { gradle.includedBuild(it).task(":test") })
}

tasks.register("build") {
    group = "build"
    description = "Builds all service builds."
    dependsOn(serviceBuilds.map { gradle.includedBuild(it).task(":build") })
}
