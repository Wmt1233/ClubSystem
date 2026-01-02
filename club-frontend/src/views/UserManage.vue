<template>
  <el-card>
    <template #header><h3>用户管理 (System Admin)</h3></template>
    <el-table :data="users" stripe>
      <el-table-column prop="id" label="ID" width="50" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'primary'">{{ scope.row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="banned" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.banned ? 'danger' : 'success'">
            {{ scope.row.banned ? '已封禁' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button 
            size="small" 
            :type="scope.row.banned ? 'success' : 'danger'" 
            @click="toggleBan(scope.row)"
            v-if="scope.row.role !== 'admin'"
          >
            {{ scope.row.banned ? '解封' : '封号' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const users = ref([])

const fetchUsers = async () => {
  const res = await request.get('/admin/users')
  users.value = res.data
}

const toggleBan = async (row) => {
  await request.put(`/admin/users/${row.id}/ban`)
  ElMessage.success('操作成功')
  fetchUsers() // 刷新列表
}

onMounted(fetchUsers)
</script>