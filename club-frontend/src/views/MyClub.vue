<template>
  <el-card>
    <template #header><h3>👥 我的社团成员管理 (Manager)</h3></template>
    <el-table :data="members" stripe style="width: 100%">
      <el-table-column prop="studentName" label="学生姓名" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'APPROVED' ? 'success' : 'warning'">
            {{ scope.row.status === 'APPROVED' ? '正式成员' : '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'PENDING'" type="primary" size="small" @click="approve(scope.row.id)">批准加入</el-button>
          <span v-else style="color: #67C23A; font-size: 12px;">已通过</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const members = ref([])
const userId = localStorage.getItem('userId')

const fetchMembers = async () => {
  try {
    const res = await request.get(`/clubs/my-members?managerId=${userId}`)
    members.value = res.data
  } catch (error) { console.error(error) }
}

const approve = async (memberId) => {
  await request.put(`/clubs/members/${memberId}/approve`)
  ElMessage.success('操作成功')
  fetchMembers()
}

onMounted(fetchMembers)
</script>