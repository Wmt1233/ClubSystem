<template>
  <el-card>
    <template #header>
      <h3>📝 建社申请审批 (System Admin)</h3>
    </template>

    <el-table :data="applications" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="applicantName" label="申请人" width="100" />
      <el-table-column prop="name" label="拟定社团名" width="150" />
      <el-table-column prop="category" label="分类" width="100">
        <template #default="scope">
          <el-tag>{{ scope.row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="申请理由" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="success" size="small" @click="handleApprove(scope.row.id)">通过</el-button>
          <el-button type="danger" size="small" @click="handleReject(scope.row.id)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="applications.length === 0" description="暂无待审核申请" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const applications = ref([])
const loading = ref(false)

const fetchApplications = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/applications')
    applications.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    const res = await request.post(`/admin/applications/${id}/approve`)
    ElMessage.success(res.data || '审批通过')
    fetchApplications() // 刷新列表
  } catch (error) {
    console.error(error)
  }
}

const handleReject = async (id) => {
  try {
    await request.post(`/admin/applications/${id}/reject`)
    ElMessage.warning('已驳回')
    fetchApplications()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchApplications)
</script>