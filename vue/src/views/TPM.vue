<template>
  <div class="tpm-container">
    <h1>TPM</h1>
    <el-form :model="form" label-width="180px">
      <el-form-item label="Expression Matrix File Path">
        <el-input v-model="form.expressionMatrix" placeholder="Enter the path to the expression matrix file"></el-input>
      </el-form-item>
      <el-form-item label="Annotation File Path">
        <el-input v-model="form.annotationFile" placeholder="Enter the path to the annotation file"></el-input>
      </el-form-item>
      <el-form-item label="Output Directory">
        <el-input v-model="form.outputDir" placeholder="Enter the output directory path"></el-input>
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
        annotationFile: '',
        outputDir: '',
      },
    };
  },
  methods: {
    submitForm() {
      axios.post('http://localhost:9090/api/tpm', this.form)
        .then(response => {
          if (response.data.status === 'success') {
            this.$message.success('TPM calculation executed successfully!');
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
.tpm-container {
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
