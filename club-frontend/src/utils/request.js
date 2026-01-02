import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    // 注意：这里必须对应你 Spring Boot 的端口
    baseURL: '/api', 
    timeout: 5000
})

// 请求拦截器：自动添加 Token
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器：处理错误
request.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 403) {
            ElMessage.error('登录已过期或权限不足')
            localStorage.clear()
            window.location.href = '/login'
        } else {
            ElMessage.error(error.response?.data?.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

export default request