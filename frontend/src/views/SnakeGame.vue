<template>
  <div class="snake-game-page">
    <el-card class="game-card">
      <template #header>
        <div class="game-header">
          <span class="game-title">🐍 贪吃蛇</span>
          <div class="game-info">
            <el-tag type="success" size="large">得分：{{ score }}</el-tag>
            <el-tag v-if="highScore > 0" type="warning" size="large">最高：{{ highScore }}</el-tag>
          </div>
        </div>
      </template>

      <div class="game-area">
        <!-- 游戏画布 -->
        <canvas
          ref="canvasRef"
          :width="canvasSize"
          :height="canvasSize"
          class="game-canvas"
          tabindex="0"
          @keydown.prevent="handleKeydown"
        />

        <!-- 覆盖层 -->
        <div v-if="gameState !== 'playing'" class="game-overlay">
          <template v-if="gameState === 'idle'">
            <h3>🐍 贪吃蛇</h3>
            <p class="hint">方向键 / WASD 控制方向</p>
            <p class="hint">吃到 ¥ 得分，撞墙或撞到自己游戏结束</p>
            <el-button type="primary" size="large" @click="startGame">
              开始游戏
            </el-button>
          </template>

          <template v-if="gameState === 'paused'">
            <h3>⏸️ 已暂停</h3>
            <el-button type="primary" size="large" @click="resumeGame">
              继续游戏
            </el-button>
          </template>

          <template v-if="gameState === 'over'">
            <h3>💀 游戏结束</h3>
            <p v-if="score === highScore && score > 0" class="new-record">🎉 新纪录！</p>
            <p class="final-score">最终得分：<strong>{{ score }}</strong></p>
            <div class="over-actions">
              <el-button type="primary" size="large" @click="startGame">
                再来一局
              </el-button>
              <el-button size="large" @click="resetToIdle">
                返回
              </el-button>
            </div>
          </template>
        </div>
      </div>

      <div class="game-controls">
        <el-button-group>
          <el-button :disabled="gameState !== 'playing'" @click="pauseGame">
            <el-icon><VideoPause /></el-icon> 暂停
          </el-button>
          <el-button :disabled="gameState !== 'paused'" @click="resumeGame">
            <el-icon><VideoPlay /></el-icon> 继续
          </el-button>
          <el-button @click="startGame">
            <el-icon><Refresh /></el-icon> 重新开始
          </el-button>
        </el-button-group>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

// ==================== 游戏配置 ====================
const GRID_SIZE = 20          // 格子数
const CELL_SIZE = 20          // 每格像素
const canvasSize = GRID_SIZE * CELL_SIZE  // 400px
const BASE_SPEED = 130        // 初始速度 (ms)

// ==================== 响应式状态 ====================
const canvasRef = ref(null)
const gameState = ref('idle') // idle | playing | paused | over
const score = ref(0)
const highScore = ref(parseInt(localStorage.getItem('snake_high_score') || '0', 10))

// ==================== 按键映射（模块级常量，避免每次按键重分配） ====================
const KEY_MAP = {
  ArrowUp:    { x: 0, y: -1 },
  ArrowDown:  { x: 0, y: 1 },
  ArrowLeft:  { x: -1, y: 0 },
  ArrowRight: { x: 1, y: 0 },
  w: { x: 0, y: -1 },
  s: { x: 0, y: 1 },
  a: { x: -1, y: 0 },
  d: { x: 1, y: 0 },
}

// ==================== 游戏内部变量 ====================
let snake = []
let food = { x: 0, y: 0 }
let direction = { x: 1, y: 0 }   // 当前方向
let nextDirection = { x: 1, y: 0 }
let gameLoop = null
let ctx = null

// ==================== 游戏逻辑 ====================

function initSnake() {
  const mid = Math.floor(GRID_SIZE / 2)
  snake = [
    { x: mid, y: mid },
    { x: mid - 1, y: mid },
    { x: mid - 2, y: mid },
  ]
}

function randomFood() {
  const occupied = new Set(snake.map(s => `${s.x},${s.y}`))
  const totalCells = GRID_SIZE * GRID_SIZE
  if (occupied.size >= totalCells) return null  // 棋盘已满（理论上游戏胜利）

  // 随机尝试有限次数
  const maxAttempts = 100
  for (let attempt = 0; attempt < maxAttempts; attempt++) {
    const pos = {
      x: Math.floor(Math.random() * GRID_SIZE),
      y: Math.floor(Math.random() * GRID_SIZE),
    }
    if (!occupied.has(`${pos.x},${pos.y}`)) return pos
  }

  // 退路：线性扫描找第一个空位
  for (let y = 0; y < GRID_SIZE; y++) {
    for (let x = 0; x < GRID_SIZE; x++) {
      if (!occupied.has(`${x},${y}`)) return { x, y }
    }
  }
  return null
}

function draw() {
  if (!ctx) return

  // 背景
  ctx.fillStyle = '#1a1a2e'
  ctx.fillRect(0, 0, canvasSize, canvasSize)

  // 网格线（淡色）
  ctx.strokeStyle = 'rgba(255,255,255,0.03)'
  ctx.lineWidth = 0.5
  for (let i = 0; i <= GRID_SIZE; i++) {
    ctx.beginPath()
    ctx.moveTo(i * CELL_SIZE, 0)
    ctx.lineTo(i * CELL_SIZE, canvasSize)
    ctx.stroke()
    ctx.beginPath()
    ctx.moveTo(0, i * CELL_SIZE)
    ctx.lineTo(canvasSize, i * CELL_SIZE)
    ctx.stroke()
  }

  // 食物（金色 ¥ 符号）
  ctx.fillStyle = '#f0c040'
  ctx.font = 'bold 16px monospace'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText('¥', food.x * CELL_SIZE + CELL_SIZE / 2, food.y * CELL_SIZE + CELL_SIZE / 2)

  // 蛇身
  snake.forEach((seg, i) => {
    const x = seg.x * CELL_SIZE
    const y = seg.y * CELL_SIZE

    // 身体渐变
    if (i === 0) {
      ctx.fillStyle = '#4ecb71'  // 蛇头绿色
    } else {
      const ratio = i / snake.length
      const r = Math.floor(30 + ratio * 20)
      const g = Math.floor(140 + ratio * 40)
      const b = Math.floor(60 + ratio * 80)
      ctx.fillStyle = `rgb(${r},${g},${b})`
    }

    // 圆角方块
    const padding = 1
    const size = CELL_SIZE - padding * 2
    const radius = i === 0 ? 5 : 3
    drawRoundedRect(ctx, x + padding, y + padding, size, size, radius)
    ctx.fill()

    // 蛇头眼睛
    if (i === 0) {
      ctx.fillStyle = '#fff'
      const ex = x + CELL_SIZE / 2
      const ey = y + CELL_SIZE / 2
      const offset = 4
      if (direction.x === 1) {
        ctx.fillRect(ex + 2, ey - offset - 2, 3, 3)
        ctx.fillRect(ex + 2, ey + offset - 1, 3, 3)
      } else if (direction.x === -1) {
        ctx.fillRect(ex - 5, ey - offset - 2, 3, 3)
        ctx.fillRect(ex - 5, ey + offset - 1, 3, 3)
      } else if (direction.y === -1) {
        ctx.fillRect(ex - offset - 2, ey - 5, 3, 3)
        ctx.fillRect(ex + offset - 1, ey - 5, 3, 3)
      } else if (direction.y === 1) {
        ctx.fillRect(ex - offset - 2, ey + 2, 3, 3)
        ctx.fillRect(ex + offset - 1, ey + 2, 3, 3)
      }
    }
  })
}

function drawRoundedRect(ctx, x, y, w, h, r) {
  ctx.beginPath()
  ctx.moveTo(x + r, y)
  ctx.lineTo(x + w - r, y)
  ctx.arcTo(x + w, y, x + w, y + r, r)
  ctx.lineTo(x + w, y + h - r)
  ctx.arcTo(x + w, y + h, x + w - r, y + h, r)
  ctx.lineTo(x + r, y + h)
  ctx.arcTo(x, y + h, x, y + h - r, r)
  ctx.lineTo(x, y + r)
  ctx.arcTo(x, y, x + r, y, r)
  ctx.closePath()
}

function move() {
  // 应用方向
  direction = { ...nextDirection }

  // 计算新头部
  const head = snake[0]
  const newHead = { x: head.x + direction.x, y: head.y + direction.y }

  // 撞墙检测
  if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE) {
    endGame()
    return
  }

  // 撞自己检测
  if (snake.some(s => s.x === newHead.x && s.y === newHead.y)) {
    endGame()
    return
  }

  // 添加新头部
  snake.unshift(newHead)

  // 吃到食物
  if (newHead.x === food.x && newHead.y === food.y) {
    score.value += 10
    const newFood = randomFood()
    if (!newFood) {
      // 棋盘已满，玩家胜利
      endGame()
      return
    }
    food = newFood
  } else {
    snake.pop()
  }

  draw()
  scheduleNext()
}

function scheduleNext() {
  const speed = Math.max(50, BASE_SPEED - Math.floor(score.value / 50) * 10)
  gameLoop = setTimeout(move, speed)
}

function endGame() {
  gameState.value = 'over'
  clearTimeout(gameLoop)
  gameLoop = null

  // 更新最高分
  if (score.value > highScore.value) {
    highScore.value = score.value
    localStorage.setItem('snake_high_score', String(score.value))
  }
}

// ==================== 控制方法 ====================

/** 重置棋盘状态（蛇、方向、食物、分数） */
function resetBoard() {
  clearTimeout(gameLoop)
  gameLoop = null
  initSnake()
  direction = { x: 1, y: 0 }
  nextDirection = { x: 1, y: 0 }
  score.value = 0
  food = randomFood()
}

function startGame() {
  resetBoard()
  gameState.value = 'playing'
  nextTick(() => {
    if (canvasRef.value) {
      canvasRef.value.focus()
    }
    draw()
    scheduleNext()
  })
}

function pauseGame() {
  if (gameState.value !== 'playing') return
  clearTimeout(gameLoop)
  gameLoop = null
  gameState.value = 'paused'
}

function resumeGame() {
  if (gameState.value !== 'paused') return
  gameState.value = 'playing'
  nextTick(() => {
    if (canvasRef.value) {
      canvasRef.value.focus()
    }
    draw()
    scheduleNext()
  })
}

function resetToIdle() {
  resetBoard()
  gameState.value = 'idle'
  nextTick(() => draw())
}

function handleKeydown(e) {
  if (gameState.value !== 'playing') return

  const newDir = KEY_MAP[e.key.toLowerCase()]
  if (!newDir) return

  // 禁止反向（不能掉头）
  if (newDir.x === -direction.x && newDir.y === -direction.y && snake.length > 1) return

  nextDirection = newDir
}

// ==================== 生命周期 ====================

onMounted(() => {
  ctx = canvasRef.value.getContext('2d')
  initSnake()
  food = randomFood()
  draw()
})

onUnmounted(() => {
  clearTimeout(gameLoop)
})
</script>

<style scoped>
.snake-game-page {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.game-card {
  width: 480px;
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.game-title {
  font-size: 18px;
  font-weight: bold;
}

.game-info {
  display: flex;
  gap: 10px;
}

.game-area {
  position: relative;
  display: flex;
  justify-content: center;
  outline: none;
}

.game-canvas {
  border-radius: 8px;
  outline: none;
  cursor: pointer;
}

.game-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.75);
  border-radius: 8px;
  color: #fff;
  gap: 12px;
}

.game-overlay h3 {
  margin: 0;
  font-size: 24px;
}

.hint {
  margin: 0;
  font-size: 13px;
  color: #ccc;
}

.new-record {
  font-size: 20px;
  color: #f0c040;
  animation: pulse 0.6s infinite alternate;
}

@keyframes pulse {
  from { transform: scale(1); }
  to { transform: scale(1.15); }
}

.final-score {
  font-size: 18px;
  margin: 0;
}

.over-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.game-controls {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
