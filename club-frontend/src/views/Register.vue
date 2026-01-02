<template>
  <div class="login-bg">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>📚 学生账号注册</h2>
        </div>
      </template>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input type="password" v-model="form.password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-input type="password" v-model="confirmPassword" placeholder="请确认密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        
        <el-button type="success" :loading="loading" @click="handleRegister" style="width: 100%;">
          立即注册
        </el-button>
        <div style="margin-top: 10px; text-align: center;">
            <el-button link type="primary" @click="$router.push('/login')">已有账号？去登录</el-button>
        </div>
      </el-form>
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
const confirmPassword = ref('')

const handleRegister = async () => {
  if(!form.value.username || !form.value.password) return ElMessage.warning('请输入账号密码')
  if(form.value.password !== confirmPassword.value) return ElMessage.warning('两次输入密码不一致')
  
  loading.value = true
  try {
    // 调用后端注册接口
    await request.post('/register', form.value)
    ElMessage.success('注册成功，请登录')
    // 注册成功后跳转回登录页
    router.push('/login')
  } catch (err) {
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
  background: linear-gradient(135deg, #42d392 0%, #647eff 100%);
}
.login-card { width: 380px; border-radius: 10px; }
.card-header h2 { text-align: center; margin: 0; color: #333; }
</style>