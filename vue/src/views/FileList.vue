<template>
  <div>
    <h1>File List</h1>
    <el-input v-model="searchQuery" placeholder="Search Files" @input="fetchFileList"></el-input>
    <el-table :data="filteredFiles" style="width: 100%">
      <el-table-column prop="fileName" label="File Name" width="180"></el-table-column>
      <el-table-column prop="filePath" label="File Path"></el-table-column>
      <el-table-column label="Actions" width="180">
        <template slot-scope="scope">
          <el-button @click="downloadFile(scope.row.id)" type="text" size="small">Download</el-button>
          <el-button @click="deleteFile(scope.row.id)" type="text" size="small" style="color: red;">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="totalFiles"
      :page-size="pageSize"
      @current-change="handlePageChange"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  data() {
    return {
      fileList: [],
      searchQuery: '',
      currentPage: 1,
      pageSize: 10,
      totalFiles: 0
    };
  },
  computed: {
    filteredFiles() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.fileList.slice(start, end);
    }
  },
  created() {
    this.fetchFileList();
  },
  methods: {
    async fetchFileList() {
      try {
        const response = await fetch(`http://localhost:9090/files/search?query=${this.searchQuery}`);
        const result = await response.json();
        this.fileList = result;
        this.totalFiles = result.length;
      } catch (error) {
        console.error('Failed to fetch file list:', error);
      }
    },
    async downloadFile(id) {
      try {
        const response = await fetch(`http://localhost:9090/files/download/${id}`);
        if (response.ok) {
          const blob = await response.blob();
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = id;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(url);
        } else {
          this.$message.error('File download failed');
        }
      } catch (error) {
        console.error('Failed to download file:', error);
        this.$message.error('Failed to download file');
      }
    },
    async deleteFile(id) {
      try {
        const response = await fetch(`http://localhost:9090/files/${id}`, {
          method: 'DELETE'
        });
        const result = await response.json();
        if (result.message === 'File deleted successfully') {
          this.$message.success(result.message);
          this.fetchFileList();
        } else {
          this.$message.error(result.message);
        }
      } catch (error) {
        console.error('Failed to delete file:', error);
        this.$message.error('Failed to delete file');
      }
    },
    handlePageChange(page) {
      this.currentPage = page;
    }
  }
};
</script>

<style>
</style>
