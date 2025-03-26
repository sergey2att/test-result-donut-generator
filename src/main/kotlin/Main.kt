package org.silchenko


fun main() {
    val statistic = Statistic(
        passed = 30,
        failed = 4,
        broken = 3,
        skipped = 4,
        unknown = 0,
        total = 41
    )
    TestResultDonutGenerator("Test Result Donut", statistic).generate()
}