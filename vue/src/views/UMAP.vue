<template>
    <div class="pca-container">
      <h1>UMAP Analysis</h1>
      <el-form :model="form" label-width="180px">
        <el-form-item label="Expression Matrix File Path">
          <el-input v-model="form.expressionMatrix" placeholder="Enter the path to the expression matrix file"></el-input>
        </el-form-item>
        <el-form-item label="Group Information">
          <el-input v-model="form.groupInfo" placeholder="Enter group information (e.g., group1, group2)"></el-input>
        </el-form-item>
        <el-form-item label="Output File Path">
          <el-input v-model="form.outputPath" placeholder="Enter the path to save the UMAP results"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">Execute</el-button>
        </el-form-item>
      </el-form>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        form: {
          expressionMatrix: '',
          groupInfo: '',  // 新增字段，接收分组信息
          outputPath: ''
        },
      };
    },
    methods: {
      submitForm() {
        axios.post('http://localhost:9090/umap/perform', this.form)
          .then(response => {
            if (response.data.status === 'success') {
              this.$message.success('UMAP executed successfully!');
            } else {
              this.$message.error('Error: ' + response.data.message);
            }
          })
          .catch(error => {
            this.$message.error('Execution failed: ' + error.message);
          });
      },
    },
  };
  </script>
  
  <style>
  .pca-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  h1 {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .el-input {
    width: 100%;
  }
  </style>
  