<template>
  <div class="htseq-container">
    <h1>HtseqCounts Construct Expression Matrix</h1>
    <el-form :model="form" label-width="180px">
      <el-form-item label="Quantification File 1">
        <el-input v-model="form.quantFile1" placeholder="Enter the path to quantification file 1"></el-input>
      </el-form-item>
      <el-form-item label="Quantification File 2">
        <el-input v-model="form.quantFile2" placeholder="Enter the path to quantification file 2"></el-input>
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
        quantFile1: '',
        quantFile2: '',
        outputDir: '',
      },
    };
  },
  methods: {
    submitForm() {
      axios.post('http://localhost:9090/expMatrix/Htseq', this.form)
        .then(response => {
          this.$message.success(response.data.message);
        })
        .catch(error => {
          this.$message.error('Execution failed: ' + error.message);
        });
    },
  },
};
</script>

<style>
.htseq-container {
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
