<template>
  <div class="project-config-container">
    <h1>Project Configuration</h1>
    <el-form ref="form" :model="form" label-width="150px">
      <el-form-item label="Project Name">
        <el-input v-model="form.projectName" placeholder="Enter project name"></el-input>
      </el-form-item>
      <el-form-item label="GC Content">
        <el-input v-model="form.gcContent" placeholder="Enter GC content"></el-input>
      </el-form-item>
      <el-form-item label="Alignment Rate">
        <el-input v-model="form.alignmentRate" placeholder="Enter alignment rate"></el-input>
      </el-form-item>
      <el-form-item label="Alignment Tools">
        <el-select v-model="form.alignmentTool" placeholder="Select alignment tool">
          <el-option label="STAR" value="STAR"></el-option>
          <el-option label="Hisat2" value="Hisat2"></el-option>
          <el-option label="Bowtie2" value="Bowtie2"></el-option>
          <el-option label="BWA" value="BWA"></el-option>
          <el-option label="Minimap2" value="Minimap2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Quantitative Tools">
        <el-select v-model="form.quantificationTool" placeholder="Select a quantitative tool">
          <el-option label="FeatureCounts" value="FeatureCounts"></el-option>
          <el-option label="Stringtie" value="Stringtie"></el-option>
          <el-option label="HTSeq" value="HTSeq"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Sequencing File 1">
        <el-select v-model="form.sequenceFile1" placeholder="Select sequencing file 1">
          <el-option v-for="file in remoteFiles" :key="file" :label="file" :value="file"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Sequencing File 2">
        <el-select v-model="form.sequenceFile2" placeholder="Select sequencing file 2">
          <el-option v-for="file in remoteFiles" :key="file" :label="file" :value="file"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Annotation Information">
        <el-select v-model="form.annotationFile" placeholder="Select annotation information">
          <el-option v-for="file in remoteFiles" :key="file" :label="file" :value="file"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Reference Genome">
        <el-select v-model="form.referenceGenome" placeholder="Select a reference genome">
          <el-option v-for="file in remoteFiles" :key="file" :label="file" :value="file"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">Submit</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        projectName: '',
        gcContent: '',
        alignmentRate: '',
        alignmentTool: '',
        quantificationTool: '',
        sequenceFile1: '',
        sequenceFile2: '',
        annotationFile: '',
        referenceGenome: ''
      },
      remoteFiles: []
    };
  },
  created() {
    this.fetchRemoteFiles();
  },
  methods: {
    async fetchRemoteFiles() {
      try {
        const response = await fetch('http://localhost:9090/files');
        const files = await response.json();
        this.remoteFiles = files.map(file => file.fileName);
      } catch (error) {
        console.error('Failed to fetch remote files:', error);
      }
    },
    async submitForm() {
      try {
        const response = await fetch('http://localhost:9090/project-config', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.form)
        });
        const result = await response.json();
        this.$message.success('Submission successful: ' + result.message);
      } catch (error) {
        console.error('Submission failed:', error);
        this.$message.error('Submission failed');
      }
    }
  }
};
</script>

<style scoped>
.project-config-container {
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
