<template>
  <div class="login-bg">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>社团管理系统</h2>
        </div>
      </template>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input type="password" v-model="form.password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%; margin-top: 10px;">
          登录
        </el-button>

        <div style="margin-top: 10px; text-align: center;">
    <el-button link type="primary" @click="$router.push('/register')">没有账号？去注册</el-button>
</div>
      </el-form>
      <div class="tips">
        
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
  if(!form.value.username || !form.value.password) return ElMessage.warning('请输入账号密码')
  
  loading.value = true
  try {
    const res = await request.post('/login', form.value)
    // 根据后端返回结构保存数据
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('role', res.data.role)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('userId', res.data.userId)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (err) {
    // 错误已在 request.js 拦截处理
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card { width: 380px; border-radius: 10px; }
.card-header h2 { text-align: center; margin: 0; color: #333; }
.tips { margin-top: 20px; font-size: 12px; color: #999; text-align: center; }
</style>
