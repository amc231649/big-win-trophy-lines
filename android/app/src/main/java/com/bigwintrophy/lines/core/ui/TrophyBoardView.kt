package com.bigwintrophy.lines.core.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.data.sample.SampleData
import com.bigwintrophy.lines.domain.model.CellPoint
import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.TrophyPair
import com.bigwintrophy.lines.presentation.game.BoardEngine

class TrophyBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnBoardEvent {
        fun onMoveMade(moves: Int, connected: Int, total: Int)
        fun onPairClosed(connected: Int, total: Int)
        fun onSolved()
    }

    var listener: OnBoardEvent? = null
    var isPreview: Boolean = false
    var highContrast: Boolean = false

    private var engine: BoardEngine? = null
    private var tileSize: Int = 0
    private var lastTouchedCell: CellPoint? = null
    private var pulsePhase: Float = 0f
    private var flashAlpha: Float = 0f
    private var previousConnected: Int = 0

    private val density = resources.displayMetrics.density
    private val framePx = ((BOARD_PAD_DP + BOARD_BORDER_DP) * density).toInt()

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glossPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val flashPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val padPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val workRect = RectF()
    private val workPath = Path()
    private val spriteCache = HashMap<String, Drawable?>()

    private var pulseAnimator: ValueAnimator? = null
    private var flashAnimator: ValueAnimator? = null

    private val pairColors = intArrayOf(
        ContextCompat.getColor(context, R.color.trophy_gold),
        ContextCompat.getColor(context, R.color.award_red),
        ContextCompat.getColor(context, R.color.line_azure),
        ContextCompat.getColor(context, R.color.pair_mint),
        ContextCompat.getColor(context, R.color.pair_violet),
        ContextCompat.getColor(context, R.color.pair_amber)
    )

    init {
        setBackgroundResource(R.drawable.board_frame)
        cellPaint.style = Paint.Style.FILL
        cellPaint.color = ContextCompat.getColor(context, R.color.navy_800)
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = density
        gridPaint.color = ContextCompat.getColor(context, R.color.board_grid)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeJoin = Paint.Join.ROUND
        glossPaint.style = Paint.Style.STROKE
        glossPaint.strokeCap = Paint.Cap.ROUND
        glossPaint.strokeJoin = Paint.Join.ROUND
        glossPaint.color = GLOSS_COLOR
        ringPaint.style = Paint.Style.STROKE
        ringPaint.strokeWidth = 2f * density
        flashPaint.style = Paint.Style.FILL
        flashPaint.color = ContextCompat.getColor(context, R.color.trophy_gold)
        padPaint.style = Paint.Style.FILL
    }

    fun setBoard(board: LevelBoard) {
        val created = BoardEngine(board)
        engine = created
        previousConnected = 0
        lastTouchedCell = null
        requestLayout()
        invalidate()
    }

    fun engineOrNull(): BoardEngine? = engine

    fun resetBoard() {
        engine?.reset()
        previousConnected = 0
        invalidate()
        notifyMove()
    }

    fun undoMove(): Boolean {
        val changed = engine?.undo() ?: false
        if (changed) {
            invalidate()
            notifyMove()
        }
        return changed
    }

    fun applyHint(pair: TrophyPair): Boolean {
        val changed = engine?.applyHint(pair) ?: false
        if (changed) {
            invalidate()
            notifyMove()
        }
        return changed
    }

    fun playFlash() {
        flashAnimator?.cancel()
        val animator = ValueAnimator.ofFloat(0f, FLASH_PEAK, 0f)
        animator.duration = FLASH_MS
        animator.addUpdateListener { value ->
            flashAlpha = value.animatedValue as Float
            invalidate()
        }
        flashAnimator = animator
        animator.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startPulse()
    }

    override fun onDetachedFromWindow() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        flashAnimator?.cancel()
        flashAnimator = null
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxBoard = (BOARD_MAX_DP * density).toInt()
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var available = maxBoard
        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize in 1 until available) {
            available = widthSize
        }
        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize in 1 until available) {
            available = heightSize
        }
        val cols = engine?.board?.cols ?: DEFAULT_COLS
        var tile = (available - 2 * framePx) / cols
        if (tile < MIN_TILE) tile = MIN_TILE
        tileSize = tile
        val size = tile * cols + 2 * framePx
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val current = engine ?: return
        if (tileSize <= 0) return
        drawCells(canvas, current)
        drawPaths(canvas, current)
        drawEndpoints(canvas, current)
        drawHeadRing(canvas, current)
        if (flashAlpha > 0f) {
            flashPaint.alpha = (flashAlpha * MAX_ALPHA).toInt()
            workRect.set(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawRoundRect(workRect, framePx.toFloat(), framePx.toFloat(), flashPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isPreview) return false
        val current = engine ?: return false
        val cell = cellAt(event.x, event.y) ?: return true
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchedCell = cell
                handleCell(current, cell)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (cell != lastTouchedCell) {
                    lastTouchedCell = cell
                    handleCell(current, cell)
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                lastTouchedCell = null
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun handleCell(current: BoardEngine, cell: CellPoint) {
        val changed = current.tap(cell)
        if (!changed) return
        invalidate()
        notifyMove()
    }

    private fun notifyMove() {
        val current = engine ?: return
        val connected = current.connectedCount()
        val total = current.board.pairs.size
        listener?.onMoveMade(current.moves, connected, total)
        if (connected > previousConnected) {
            listener?.onPairClosed(connected, total)
        }
        previousConnected = connected
        if (current.isSolved()) {
            playFlash()
            listener?.onSolved()
        }
    }

    private fun startPulse() {
        pulseAnimator?.cancel()
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = PULSE_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { value ->
            pulsePhase = value.animatedValue as Float
            val current = engine
            if (current != null && current.activePairId() != BoardEngine.NO_PAIR) invalidate()
        }
        pulseAnimator = animator
        animator.start()
    }

    private fun drawCells(canvas: Canvas, current: BoardEngine) {
        val inset = tileSize * CELL_INSET
        val radius = tileSize * CELL_RADIUS
        for (row in 0 until current.board.rows) {
            for (col in 0 until current.board.cols) {
                val left = framePx + col * tileSize + inset
                val top = framePx + row * tileSize + inset
                workRect.set(left, top, left + tileSize - 2 * inset, top + tileSize - 2 * inset)
                canvas.drawRoundRect(workRect, radius, radius, cellPaint)
                canvas.drawRoundRect(workRect, radius, radius, gridPaint)
            }
        }
    }

    private fun drawPaths(canvas: Canvas, current: BoardEngine) {
        val strokeWidth = tileSize * LINE_WIDTH
        val glossWidth = tileSize * GLOSS_WIDTH
        for (pair in current.board.pairs) {
            val path = current.pathOf(pair.id)
            if (path.size < 2) continue
            workPath.reset()
            val first = path.first()
            workPath.moveTo(centerX(first), centerY(first))
            for (index in 1 until path.size) {
                val cell = path[index]
                workPath.lineTo(centerX(cell), centerY(cell))
            }
            linePaint.color = colorFor(pair.colorIndex)
            linePaint.strokeWidth = if (highContrast) strokeWidth * CONTRAST_BOOST else strokeWidth
            canvas.drawPath(workPath, linePaint)
            glossPaint.strokeWidth = glossWidth
            canvas.save()
            canvas.translate(0f, -tileSize * GLOSS_OFFSET)
            canvas.drawPath(workPath, glossPaint)
            canvas.restore()
        }
    }

    private fun drawEndpoints(canvas: Canvas, current: BoardEngine) {
        val spriteSize = (tileSize * SPRITE_SCALE).toInt()
        val padRadius = tileSize * PAD_RADIUS
        for (pair in current.board.pairs) {
            drawEndpoint(canvas, pair, pair.start, spriteSize, padRadius)
            drawEndpoint(canvas, pair, pair.end, spriteSize, padRadius)
        }
    }

    private fun drawEndpoint(
        canvas: Canvas,
        pair: TrophyPair,
        cell: CellPoint,
        spriteSize: Int,
        padRadius: Float
    ) {
        val cx = centerX(cell)
        val cy = centerY(cell)
        padPaint.color = colorFor(pair.colorIndex)
        padPaint.alpha = PAD_ALPHA
        canvas.drawCircle(cx, cy, padRadius, padPaint)
        val drawable = spriteFor(pair.spriteKey) ?: return
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        var drawWidth = spriteSize
        var drawHeight = spriteSize
        if (intrinsicWidth > 0 && intrinsicHeight > 0) {
            if (intrinsicWidth >= intrinsicHeight) {
                drawHeight = spriteSize * intrinsicHeight / intrinsicWidth
            } else {
                drawWidth = spriteSize * intrinsicWidth / intrinsicHeight
            }
        }
        val left = (cx - drawWidth / 2f).toInt()
        val top = (cy - drawHeight / 2f).toInt()
        drawable.setBounds(left, top, left + drawWidth, top + drawHeight)
        drawable.draw(canvas)
    }

    private fun drawHeadRing(canvas: Canvas, current: BoardEngine) {
        val activeId = current.activePairId()
        if (activeId == BoardEngine.NO_PAIR) return
        val headCell = current.head() ?: return
        val pair = current.board.pairs.firstOrNull { it.id == activeId } ?: return
        ringPaint.color = colorFor(pair.colorIndex)
        val base = tileSize * RING_BASE
        val radius = base + tileSize * RING_SWING * pulsePhase
        canvas.drawCircle(centerX(headCell), centerY(headCell), radius, ringPaint)
    }

    private fun spriteFor(key: String): Drawable? {
        if (spriteCache.containsKey(key)) return spriteCache[key]
        val resId = when (key) {
            SampleData.SPRITE_TROPHY -> R.drawable.sprite_trophy_gold
            SampleData.SPRITE_MEDAL -> R.drawable.sprite_medal_silver
            SampleData.SPRITE_STAR -> R.drawable.sprite_star_ruby
            SampleData.SPRITE_CROWN -> R.drawable.sprite_crown_royal
            SampleData.SPRITE_CUP -> R.drawable.sprite_cup_azure
            SampleData.SPRITE_RIBBON -> R.drawable.sprite_ribbon_rose
            else -> R.drawable.sprite_trophy_gold
        }
        val drawable = ContextCompat.getDrawable(context, resId)
        spriteCache[key] = drawable
        return drawable
    }

    private fun colorFor(index: Int): Int {
        if (index < 0) return pairColors[0]
        return pairColors[index % pairColors.size]
    }

    private fun centerX(cell: CellPoint): Float =
        framePx + cell.col * tileSize + tileSize / 2f

    private fun centerY(cell: CellPoint): Float =
        framePx + cell.row * tileSize + tileSize / 2f

    private fun cellAt(x: Float, y: Float): CellPoint? {
        val current = engine ?: return null
        if (tileSize <= 0) return null
        val col = ((x - framePx) / tileSize).toInt()
        val row = ((y - framePx) / tileSize).toInt()
        if (x < framePx || y < framePx) return null
        if (col < 0 || row < 0) return null
        if (col >= current.board.cols || row >= current.board.rows) return null
        return CellPoint(col, row)
    }

    private companion object {
        const val BOARD_MAX_DP = 380f
        const val BOARD_PAD_DP = 6f
        const val BOARD_BORDER_DP = 2f
        const val DEFAULT_COLS = 5
        const val MIN_TILE = 24
        const val CELL_INSET = 0.06f
        const val CELL_RADIUS = 0.16f
        const val LINE_WIDTH = 0.34f
        const val GLOSS_WIDTH = 0.12f
        const val GLOSS_OFFSET = 0.06f
        const val CONTRAST_BOOST = 1.15f
        const val SPRITE_SCALE = 0.62f
        const val PAD_RADIUS = 0.42f
        const val PAD_ALPHA = 56
        const val RING_BASE = 0.30f
        const val RING_SWING = 0.10f
        const val PULSE_MS = 900L
        const val FLASH_MS = 300L
        const val FLASH_PEAK = 0.55f
        const val MAX_ALPHA = 255
        const val GLOSS_COLOR = 0x40FFFFFF
    }
}
