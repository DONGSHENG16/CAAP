<template>
  <div class="project-search-container">
    <h1>Project Search</h1>
    <div class="search-bar">
      <el-input v-model="searchQuery" placeholder="Search Projects" @input="fetchProjects" clearable></el-input>
      <el-button type="primary" @click="fetchProjects">Search</el-button>
    </div>
    <el-table :data="filteredProjects" border stripe class="project-table">
      <el-table-column prop="projectName" label="Project Name" width="180"></el-table-column>
      <el-table-column prop="formattedCreateTime" label="Creation Time" width="180"></el-table-column>
      <el-table-column label="Actions" width="300">
        <template slot-scope="scope">
          <el-button @click="showProjectDetails(scope.row)" type="text" size="small">View Details</el-button>
          <el-button @click="executeProject(scope.row.id)" type="text" size="small">Execute Project</el-button>
          <el-button @click="downloadResults(scope.row.id)" type="text" size="small">Download Results</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogVisible" title="Project Details">
      <el-form :model="selectedProject" label-width="150px">
        <el-form-item label="Project Name">
          <el-input v-model="selectedProject.projectName" placeholder="Enter project name"></el-input>
        </el-form-item>
        <el-form-item label="GC Content">
          <el-input v-model="selectedProject.gcContent" placeholder="Enter GC content"></el-input>
        </el-form-item>
        <el-form-item label="Alignment Rate">
          <el-input v-model="selectedProject.alignmentRate" placeholder="Enter alignment rate"></el-input>
        </el-form-item>
        <el-form-item label="Alignment Tool">
          <el-input v-model="selectedProject.alignmentTool" placeholder="Enter alignment tool"></el-input>
        </el-form-item>
        <el-form-item label="Quantification Tool">
          <el-input v-model="selectedProject.quantificationTool" placeholder="Enter quantification tool"></el-input>
        </el-form-item>
        <el-form-item label="Sequence File 1">
          <el-input v-model="selectedProject.sequenceFile1" placeholder="Enter sequence file 1"></el-input>
        </el-form-item>
        <el-form-item label="Sequence File 2">
          <el-input v-model="selectedProject.sequenceFile2" placeholder="Enter sequence file 2"></el-input>
        </el-form-item>
        <el-form-item label="Annotation Information">
          <el-input v-model="selectedProject.annotationFile" placeholder="Enter annotation information"></el-input>
        </el-form-item>
        <el-form-item label="Reference Genome">
          <el-input v-model="selectedProject.referenceGenome" placeholder="Enter reference genome"></el-input>
        </el-form-item>
        <el-form-item label="Creation Time">
          <el-input :value="formatDateTime(selectedProject.createTime)" readonly></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Close</el-button>
        <el-button type="primary" @click="updateProject">Save Changes</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: '',
      projects: [],
      dialogVisible: false,
      selectedProject: {}
    };
  },
  computed: {
    filteredProjects() {
      return this.projects.filter(project => 
        project.projectName.toLowerCase().includes(this.searchQuery.toLowerCase())
      ).map(project => ({
        ...project,
        formattedCreateTime: this.formatDateTime(project.createTime)
      }));
    }
  },
  created() {
    this.fetchProjects();
  },
  methods: {
    async fetchProjects() {
      try {
        const response = await fetch('http://localhost:9090/projects');
        const projects = await response.json();
        this.projects = projects;
      } catch (error) {
        console.error('Failed to fetch projects:', error);
      }
    },
    showProjectDetails(project) {
      this.selectedProject = { ...project };
      this.dialogVisible = true;
    },
    async executeProject(projectId) {
      try {
        const response = await fetch(`http://localhost:9090/project-data/execute?id=${projectId}`, {
          method: 'POST'
        });
        const result = await response.text();
        this.$message.success(result);
      } catch (error) {
        console.error('Failed to execute project:', error);
        this.$message.error('Failed to execute project');
      }
    },
    async updateProject() {
      try {
        const response = await fetch(`http://localhost:9090/project-data/${this.selectedProject.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.selectedProject)
        });
        const updatedProject = await response.json();
        const index = this.projects.findIndex(p => p.id === updatedProject.id);
        this.$set(this.projects, index, updatedProject);
        this.dialogVisible = false;
        this.$message.success('Project updated successfully');
      } catch (error) {
        console.error('Failed to update project:', error);
        this.$message.error('Failed to update project');
      }
    },
    async downloadResults(projectId) {
      try {
        const response = await fetch(`http://localhost:9090/project-data/download/${projectId}`, {
          method: 'GET'
        });
        if (response.ok) {
          const blob = await response.blob();
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'archive_name.zip'); // or any other extension
          document.body.appendChild(link);
          link.click();
          link.parentNode.removeChild(link);
        } else {
          this.$message.error('Download failed');
        }
      } catch (error) {
        console.error('Failed to download results:', error);
        this.$message.error('Failed to download results');
      }
    },
    formatDateTime(dateTime) {
      const date = new Date(dateTime);
      return date.toLocaleString();
    }
  }
};
</script>

<style scoped>
.project-search-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.el-input {
  flex: 1;
  margin-right: 10px;
}

.project-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
