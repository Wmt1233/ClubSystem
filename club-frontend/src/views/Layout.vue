<template>
  <el-container class="layout-container">
    <el-header>
      <div class="logo"><el-icon><School /></el-icon> 社团管理系统</div>
      <div class="user-info">
        <span class="role-tag">{{ roleText }}</span>
        <span>{{ username }}</span>
        <el-button type="danger" link @click="logout" style="margin-left: 15px;">退出</el-button>
      </div>
    </el-header>

    <el-container>
      <el-aside width="200px" style="background: #fff; border-right: 1px solid #eee;">
        <el-menu :default-active="activePath" router style="border:none;">
          <el-menu-item index="/home/events"><el-icon><Calendar /></el-icon><span>活动大厅</span></el-menu-item>
          
          <template v-if="role === 'manager'">
            <el-menu-item index="/home/my-club"><el-icon><UserFilled /></el-icon><span>成员管理</span></el-menu-item>
          </template>

          <template v-if="role === 'admin'">
            <el-menu-item index="/home/users"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
            <el-menu-item index="/home/approval"><el-icon><Checked /></el-icon><span>建社审批</span></el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-main>
        <el-alert v-if="$route.path === '/home'" :title="`欢迎您，${username}`" type="success" class="mb-20" :closable="false" />

        <div class="action-panel" v-if="$route.path.includes('/events')">
           <div v-if="role === 'student'">
             <el-button type="primary" :icon="Plus" @click="openApplyDialog">申请创建社团</el-button>
           </div>
           <div v-if="role === 'manager'"><el-tag type="success">社长请在列表发布活动</el-tag></div>
        </div>

        <router-view></router-view>

        <el-card v-if="$route.path.includes('/events')" shadow="hover" style="margin-top: 20px;">
          <template #header>
             <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>📚 所有社团列表</span>
                <el-button text @click="refreshData">刷新</el-button>
             </div>
          </template>
          <el-table :data="clubList" stripe>
            <el-table-column prop="name" label="社团名称" width="180">
                <template #default="scope">
                    <el-tag>{{ scope.row.name }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="description" label="简介" />
            
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <div v-if="role === 'student' || role === 'manager'">
                  
                  <el-tag 
                    v-if="String(scope.row.managerId) === String(currentUserId)" 
                    type="danger" 
                    effect="plain"
                  >
                    👑 我管理的
                  </el-tag>

                  <el-tag v-else-if="myMemberships[scope.row.id] === 'APPROVED'" type="success" effect="dark">
                    <el-icon><Check /></el-icon> 已加入
                  </el-tag>
                  
                  <el-tag v-else-if="myMemberships[scope.row.id] === 'PENDING'" type="warning" effect="plain">
                    <el-icon><Loading /></el-icon> 审核中
                  </el-tag>
                  
                  <el-button v-else link type="primary" size="small" @click="joinClub(scope.row.id)">
                    申请加入
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

      </el-main>
    </el-container>

    <el-dialog v-model="applyDialogVisible" title="申请创建社团" width="500px">
      <el-form :model="applyForm" label-width="80px">
        <el-form-item label="社团名称">
          <el-input v-model="applyForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团分类">
          <el-select v-model="applyForm.category" placeholder="请选择社团分类" style="width: 100%">
            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input type="textarea" v-model="applyForm.description" placeholder="请简述建社宗旨..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApplication">提交申请</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { Plus, Calendar, School, User, Checked, UserFilled, Check, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 状态
const role = ref(localStorage.getItem('role') || 'student')
const username = ref(localStorage.getItem('username') || '')
const currentUserId = ref(localStorage.getItem('userId')) // 响应式 UserID

// 弹窗
const applyDialogVisible = ref(false)
const applyForm = ref({ name: '', category: '', description: '' })

// 数据
const clubList = ref([])
const myMemberships = ref({}) 

const categoryOptions = [
  { label: '科技创新', value: '科技' },
  { label: '文化艺术', value: '艺术' },
  { label: '体育竞技', value: '体育' },
  { label: '学术研究', value: '学术' },
  { label: '公益志愿', value: '公益' },
  { label: '其他类别', value: '其他' }
]

const roleText = computed(() => {
  const map = { 'admin': '管理员', 'manager': '社长', 'student': '学生' }
  return map[role.value] || role.value
})
const activePath = computed(() => route.path)

const fetchClubs = async () => {
  try {
    const res = await request.get('/clubs')
    clubList.value = res.data
  } catch (e) {}
}

const fetchMyStatus = async () => {
  // 修改：允许 student 和 manager 查看状态
  if ((role.value !== 'student' && role.value !== 'manager') || !currentUserId.value) return
  try {
    const res = await request.get(`/clubs/my-membership-status?userId=${currentUserId.value}`)
    myMemberships.value = res.data
  } catch (e) { console.error(e) }
}

const refreshData = () => {
  fetchClubs()
  fetchMyStatus()
}

const openApplyDialog = () => { applyDialogVisible.value = true }

const submitApplication = async () => {
  if(!applyForm.value.category) return ElMessage.warning('请选择分类')
  await request.post('/clubs/apply', { ...applyForm.value, applicantId: currentUserId.value })
  ElMessage.success('申请已提交')
  applyDialogVisible.value = false
  applyForm.value = { name: '', category: '', description: '' }
}

const joinClub = async (clubId) => {
  await request.post(`/clubs/${clubId}/join`, { id: currentUserId.value, username: username.value })
  ElMessage.success('入社申请已提交')
  fetchMyStatus()
}

const logout = () => { localStorage.clear(); router.push('/login') }

onMounted(async () => {
  await fetchClubs()
  await fetchMyStatus()
})
</script>
<style scoped>
.layout-container { height: 100vh; background-color: #f5f7fa; }
.el-header { background: #fff; display: flex; justify-content: space-between; align-items: center; padding: 0 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.logo { font-size: 20px; font-weight: bold; color: #409EFF; display: flex; align-items: center; gap: 10px; }
.role-tag { background: #ecf5ff; color: #409eff; padding: 2px 8px; border-radius: 4px; font-size: 12px; margin-right: 10px; }
.action-panel { background: #fff; padding: 15px; margin-bottom: 20px; border-radius: 4px; display: flex; align-items: center; }
.mb-20 { margin-bottom: 20px; }
</style>