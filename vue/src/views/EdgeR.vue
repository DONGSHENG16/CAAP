<template>
  <div class="edger-container">
    <h1>EdgeR Differential Expression Analysis</h1>
    <el-form :model="form" label-width="180px">
      <el-form-item label="Expression Matrix File Path">
        <el-input v-model="form.expressionMatrix" placeholder="Enter the path to the expression matrix file"></el-input>
      </el-form-item>
      <el-form-item label="Group Information File Path">
        <el-input v-model="form.groupFile" placeholder="Enter the path to the group information file"></el-input>
      </el-form-item>
      <el-form-item label="Output File Path">
        <el-input v-model="form.outputPath" placeholder="Enter the output file path"></el-input>
      </el-form-item>
      <el-form-item label="Select Visualization">
        <el-select v-model="form.visualization" placeholder="Select a visualization">
          <el-option label="MA" value="MA"></el-option>
          <el-option label="EnhancedVolcano" value="EnhancedVolcano"></el-option>
          <el-option label="Heatmap" value="Heatmap"></el-option>
          <el-option label="Boxplot" value="Boxplot"></el-option>
          <el-option label="beeswarm" value="beeswarm"></el-option>
          <el-option label="violin" value="violin"></el-option>
        </el-select>
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
        groupFile: '',
        outputPath: '',
        visualization: '',
      },
    };
  },
  methods: {
    submitForm() {
      axios.post('http://localhost:9090/dea/edger', this.form)
        .then(response => {
          if (response.data.status === 'success') {
            this.$message.success('DEA executed successfully!');
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
.edger-container {
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

.el-input,
.el-select {
  width: 100%;
}
</style>
