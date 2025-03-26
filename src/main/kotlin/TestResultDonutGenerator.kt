package org.silchenko

import org.knowm.xchart.PieChart
import org.knowm.xchart.PieChartBuilder
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle
import org.knowm.xchart.internal.chartpart.ChartTitle
import org.knowm.xchart.internal.chartpart.Plot_
import org.knowm.xchart.style.Styler.LegendPosition
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class TestResultDonutGenerator(
    private val title: String,
    private val statistic: Statistic,
    private val includeSkippedIntoPassRate: Boolean = false
) {

    fun generate() {
        // Create Chart
        val chart = PieChartBuilder().width(500).height(250).title(title).build()

        // Customize Chart
        chart.styler.setLegendVisible(true)
        chart.styler.setChartTitleBoxBorderColor(Color.black)
        chart.styler.setChartTitleBoxBackgroundColor(Color(0, 222, 0))
        chart.styler.setChartTitleFont(Font(Font.SANS_SERIF, Font.BOLD, 18))
        //chart.styler.setLegendBackgroundColor(Color(240, 240, 230))
        // chart.styler.setLegendBorderColor(Color.CYAN)
        chart.styler.setLegendBackgroundColor(Color.WHITE)
        chart.styler.setLegendBorderColor(Color.WHITE)
        chart.styler.decimalPattern = "#"

        //chart.styler.setPlotContentSize(.8)
        chart.styler.labelsDistance = 0.65
        chart.styler.setDonutThickness(.21)
        chart.styler.setChartBackgroundColor(Color.WHITE)

        chart.styler.setPlotBorderColor(Color.WHITE)
        chart.styler.setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut)
        chart.styler.setSliceBorderWidth(0.0)
        chart.styler.setLegendFont(Font(Font.MONOSPACED, Font.PLAIN, 12))
        chart.styler.setLabelsFont(Font(Font.MONOSPACED, Font.PLAIN, 12))
        chart.styler.setLabelsFontColorAutomaticLight(Color.black)
        chart.styler.setLegendPosition(LegendPosition.OutsideE)


        // Series
        val passed = statistic.passed
        val failed = statistic.failed
        val broken = statistic.broken
        val skipped = statistic.skipped
        val unknown = statistic.unknown
        val total = statistic.total
        val seriesColor = mutableListOf<Color>()

        if (passed > 0) {
            chart.addSeries("$passed passed", passed)
            seriesColor.add(Color(151, 199, 98)) // Зеленый для Passed
        }
        if (failed > 0) {
            chart.addSeries("$failed failed", failed)
            seriesColor.add(Color(255, 82, 71)) // Красный для Failed
        }
        if (broken > 0) {
            chart.addSeries("$broken broken", broken)
            seriesColor.add(Color(244, 205, 81)) // Оранжевый для broken
        }
        if (skipped > 0) {
            chart.addSeries("$skipped skipped", skipped)
            seriesColor.add(Color(170, 170, 170)) // Серый для Skipped
        }
        if (unknown > 0) {
            chart.addSeries("$unknown unknown", unknown)
            seriesColor.add(Color(170, 170, 170)) // Серый для Unknown
        }
        if (total > 0) {
            seriesColor.add(Color.WHITE)
            seriesColor.add(Color.WHITE)
            chart.addSeries("__________", 0)
            chart.addSeries("$total total", 0)
        }

        chart.styler.seriesColors = seriesColor.toTypedArray()

        // Show it
        // SwingWrapper(chart).displayChart()

        // Создаем изображение диаграммы
        val chartImage = BufferedImage(500, 250, BufferedImage.TYPE_INT_ARGB)
        val graphics = chartImage.createGraphics()
        chart.paint(graphics, 500, 250)
        graphics.dispose()
        val skippedCount = if (includeSkippedIntoPassRate) skipped else 0
        val passRate = if (passed == 0) 0 else passed.toDouble() / (passed + failed + broken + skippedCount).toDouble() * 100

        addTextToCenter(chart, chartImage, "${String.format("%.1f", passRate)}%", Font("Arial", Font.PLAIN, 18), Color.BLACK)

        // Сохраняем изображение в файл
        val file = File("donut_chart_xchart.png")
        ImageIO.write(chartImage, "png", file)
        println("Диаграмма сохранена в файл: ${file.absolutePath}")
    }

    // Функция для добавления текста в центр изображения
    private fun addTextToCenter(chart: PieChart, image: BufferedImage, text: String, font: Font, color: Color) {
        val graphics = image.createGraphics() as Graphics2D
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.font = font
        graphics.color = color

        // Получаем размеры изображения

        val methodPlot = chart::class.java.superclass.getDeclaredMethod("getPlot")
        val methodChartTitle = chart::class.java.superclass.getDeclaredMethod("getChartTitle")
        methodPlot.isAccessible = true
        methodChartTitle.isAccessible = true


        val imageWidth = (methodPlot.invoke(chart) as Plot_<*, *>).bounds.width.toInt()
        val imageHeight = (methodPlot.invoke(chart) as Plot_<*, *>).bounds.height.toInt()

        val titleHeight = (methodChartTitle.invoke(chart) as ChartTitle<*, *>).bounds.height.toInt()

        // Получаем размеры текста
        val metrics = graphics.fontMetrics
        val textWidth = metrics.stringWidth(text)
        val textHeight = metrics.height

        // Вычисляем координаты для текста (центр изображения)
        val xShift = 30
        val yShift = 40
        val x = (imageWidth + xShift - textWidth) / 2
        val y = (imageHeight + titleHeight + yShift - textHeight) / 2 + metrics.ascent

        // Рисуем текст
        graphics.drawString(text, x, y)
        graphics.dispose()
    }
}
// stub
class Statistic(
    val passed: Int,
    val failed: Int,
    val broken: Int,
    val skipped: Int,
    val unknown: Int,
    val total: Int
)
