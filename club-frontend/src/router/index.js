import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue' // 【新增引入】
import Layout from '../views/Layout.vue'
import UserManage from '../views/UserManage.vue'
import EventList from '../views/EventList.vue'
import ClubApproval from '../views/ClubApproval.vue'
import MyClub from '../views/MyClub.vue'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register }, // 【新增路由】
    { 
      path: '/home', 
      component: Layout,
      children: [
        { path: '', redirect: '/home/events' },
        { path: 'users', component: UserManage },
        { path: 'events', component: EventList },
        { path: 'approval', component: ClubApproval },
        { path: 'my-club', component: MyClub }
      ]
    }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫：登录和注册页面不需要 Token
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    // 如果去的是登录页或注册页，直接放行
    if (to.path === '/login' || to.path === '/register') {
        next()
    } else {
        // 其他页面如果没有 Token，跳转登录
        if (!token) next('/login')
        else next()
    }
})

export default router