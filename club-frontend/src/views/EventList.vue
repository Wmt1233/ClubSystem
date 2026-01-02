<template>
  <div>
    <el-card style="margin-bottom: 20px;">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h3>📅 活动中心</h3>
        <el-button v-if="role === 'manager'" type="primary" @click="openPublishDialog">
          发布新活动
        </el-button>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="8" v-for="item in events" :key="item.id" style="margin-bottom: 20px;">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div style="font-weight: bold;">{{ item.title }}</div>
              <el-button 
                v-if="role === 'admin' || role === 'manager'" 
                type="danger" link size="small" @click="handleDelete(item.id)">
                删除
              </el-button>
            </div>
          </template>
          
          <p style="color: #666; font-size: 13px;">主办社团: {{ item.clubName }}</p>
          <p style="color: #666; font-size: 13px;">活动地点: {{ item.location || '线上/待定' }}</p>
          <p style="margin-top: 10px;">{{ item.content }}</p>
          
          <div style="margin-top: 15px; text-align: right;">
            <span style="font-size: 12px; color: #999; margin-right: 10px;">
              已报名: {{ item.participantIds.length }} 人
            </span>
            <el-button v-if="role === 'student'" type="primary" size="small" @click="joinEvent(item.id)">
              立即报名
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="发布活动" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="活动标题">
          <el-input v-model="form.title" placeholder="例如：迎新晚会" />
        </el-form-item>
        
        <el-form-item label="活动地点">
          <el-input v-model="form.location" placeholder="例如：第二体育馆 / 302教室" />
        </el-form-item>

        <el-form-item label="活动内容">
          <el-input type="textarea" v-model="form.content" placeholder="请输入活动详情..." />
        </el-form-item>
        
        <el-form-item label="主办社团">
          <el-select v-model="form.clubId" placeholder="请选择您的社团" style="width: 100%" @change="handleClubChange">
            <el-option
              v-for="club in myClubs"
              :key="club.id"
              :label="club.name"
              :value="club.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvent">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const role = localStorage.getItem('role')
const events = ref([])
const myClubs = ref([]) // 存储当前社长管理的社团列表
const dialogVisible = ref(false)
const form = ref({ title: '', content: '', location: '', clubId: null, clubName: '' })

// 获取所有活动
const fetchEvents = async () => {
  try {
    const res = await request.get('/events')
    events.value = res.data
  } catch (error) { console.error(error) }
}

// 获取当前社长管理的社团 (用于下拉框)
const fetchMyManagedClubs = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await request.get(`/clubs/my-managed?managerId=${userId}`)
    myClubs.value = res.data
  } catch (error) { console.error(error) }
}

// 打开弹窗时，自动加载社团列表
const openPublishDialog = () => {
  dialogVisible.value = true
  // 每次打开都重置表单
  form.value = { title: '', content: '', location: '', clubId: null, clubName: '' }
  fetchMyManagedClubs()
}

// 当下拉框选中社团时，自动填充 clubName
const handleClubChange = (val) => {
  const selectedClub = myClubs.value.find(c => c.id === val)
  if (selectedClub) {
    form.value.clubName = selectedClub.name
  }
}

// 提交发布
const submitEvent = async () => {
  if (!form.value.clubId) {
    return ElMessage.warning('请选择主办社团')
  }
  try {
    await request.post('/events', form.value)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchEvents()
  } catch (error) { console.error(error) }
}

const joinEvent = async (eventId) => {
  const currentUserId = localStorage.getItem('userId');
  if (!currentUserId) return ElMessage.error('请先登录');
  try {
    await request.post(`/events/${eventId}/join`, { userId: currentUserId })
    ElMessage.success('报名成功')
    fetchEvents()
  } catch (error) { console.error(error) }
}

const handleDelete = (eventId) => {
  ElMessageBox.confirm('确定要删除这个活动吗？', '警告', { type: 'warning' })
  .then(async () => {
    try {
      const currentUserId = localStorage.getItem('userId');
      await request.delete(`/events/${eventId}`, { params: { userId: currentUserId } })
      ElMessage.success('删除成功')
      fetchEvents()
    } catch (error) {}
  })
}

onMounted(fetchEvents)
</script>