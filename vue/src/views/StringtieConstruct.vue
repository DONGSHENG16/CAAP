<template>
  <div class="stringtie-container">
    <h1>StringTie Construct Expression Matrix</h1>
    <el-form :model="form" ref="form" label-width="180px">
      <el-form-item label="Quantification File 1">
        <el-input v-model="form.quantFile1" placeholder="Enter the path to quantification file 1"></el-input>
      </el-form-item>
      <el-form-item label="Quantification File 2">
        <el-input v-model="form.quantFile2" placeholder="Enter the path to quantification file 2"></el-input>
      </el-form-item>
      <el-form-item label="Output Directory">
        <el-input v-model="form.outputDir" placeholder="Enter the output directory path"></el-input>
      </el-form-item>
      <el-form-item label="Normalization Result">
        <el-radio-group v-model="form.normalization">
          <el-radio label="FPKM">FPKM</el-radio>
          <el-radio label="TPM">TPM</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">Execute</el-button>
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
        normalization: ''
      }
    };
  },
  methods: {
    async handleSubmit() {
      if (!this.form.normalization) {
        this.$message.error('Please select a normalization result');
        return;
      }
      try {
        const response = await axios.post('http://localhost:9090/expMatrix/stringtie', this.form);
        this.$message.success('Execution successful');
        console.log(response.data);
      } catch (error) {
        this.$message.error('Execution failed');
        console.error(error);
      }
    }
  }
};
</script>

<style scoped>
.stringtie-container {
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
