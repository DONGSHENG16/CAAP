<template>
  <div class="file-upload-container">
    <h1>File Upload</h1>
    <div class="upload-section">
      <el-upload
        class="upload-demo"
        :file-list="fileList1"
        :before-upload="beforeUpload"
        :on-progress="handleProgress1"
        :http-request="uploadRequest1"
        show-file-list>
        <el-button slot="trigger" type="primary">Select Sequencing Data 1</el-button>
        <div slot="tip" class="el-upload__tip">Sequencing Data 1</div>
      </el-upload>
      <div v-if="uploadProgress1 > 0">
        <el-progress :percentage="uploadProgress1" status="active"></el-progress>
      </div>
    </div>

    <div class="upload-section">
      <el-upload
        class="upload-demo"
        :file-list="fileList2"
        :before-upload="beforeUpload"
        :on-progress="handleProgress2"
        :http-request="uploadRequest2"
        show-file-list>
        <el-button slot="trigger" type="primary">Select Sequencing Data 2</el-button>
        <div slot="tip" class="el-upload__tip">Sequencing Data 2</div>
      </el-upload>
      <div v-if="uploadProgress2 > 0">
        <el-progress :percentage="uploadProgress2" status="active"></el-progress>
      </div>
    </div>
    <div v-if="uploadProgress2 > 0">
      <el-progress :percentage="uploadProgress2" status="active"></el-progress>
    </div>

    <div class="upload-section">
      <el-upload
        class="upload-demo"
        :file-list="fileList3"
        :before-upload="beforeUpload"
        :on-progress="handleProgress3"
        :http-request="uploadRequest3"
        show-file-list>
        <el-button slot="trigger" type="primary">Select Annotation Information</el-button>
        <div slot="tip" class="el-upload__tip">Annotation Information</div>
      </el-upload>
      <div v-if="uploadProgress3 > 0">
        <el-progress :percentage="uploadProgress3" status="active"></el-progress>
      </div>
    </div>
    <div v-if="uploadProgress3 > 0">
      <el-progress :percentage="uploadProgress3" status="active"></el-progress>
    </div>

    <div class="upload-section">
      <el-upload
        class="upload-demo"
        :file-list="fileList4"
        :before-upload="beforeUpload"
        :on-progress="handleProgress4"
        :http-request="uploadRequest4"
        show-file-list>
        <el-button slot="trigger" type="primary">Select Reference Genome</el-button>
        <div slot="tip" class="el-upload__tip">Reference Genome</div>
      </el-upload>
      <div v-if="uploadProgress4 > 0">
        <el-progress :percentage="uploadProgress4" status="active"></el-progress>
      </div>
    </div>
    <div v-if="uploadProgress4 > 0">
      <el-progress :percentage="uploadProgress4" status="active"></el-progress>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      fileList1: [],
      fileList2: [],
      fileList3: [],
      fileList4: [],
      uploadProgress1: 0,
      uploadProgress2: 0,
      uploadProgress3: 0,
      uploadProgress4: 0,
    };
  },
  methods: {
    beforeUpload(file) {
      return true;
    },
    handleProgress1(event, file, fileList) {
      this.uploadProgress1 = Math.round((event.loaded / event.total) * 100);
    },
    handleProgress2(event, file, fileList) {
      this.uploadProgress2 = Math.round((event.loaded / event.total) * 100);
    },
    handleProgress3(event, file, fileList) {
      this.uploadProgress3 = Math.round((event.loaded / event.total) * 100);
    },
    handleProgress4(event, file, fileList) {
      this.uploadProgress4 = Math.round((event.loaded / event.total) * 100);
    },
    async uploadRequest1({ file, onProgress, onSuccess, onError }) {
      this.uploadFile(file, onProgress, onSuccess, onError);
    },
    async uploadRequest2({ file, onProgress, onSuccess, onError }) {
      this.uploadFile(file, onProgress, onSuccess, onError);
    },
    async uploadRequest3({ file, onProgress, onSuccess, onError }) {
      this.uploadFile(file, onProgress, onSuccess, onError);
    },
    async uploadRequest4({ file, onProgress, onSuccess, onError }) {
      this.uploadFile(file, onProgress, onSuccess, onError);
    },
    async uploadFile(file, onProgress, onSuccess, onError) {
      const chunkSize = 5 * 1024 * 1024; // 每块大小为5MB
      const totalChunks = Math.ceil(file.size / chunkSize);
      
      for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
        const start = chunkIndex * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);

        const formData = new FormData();
        formData.append('file', chunk);
        formData.append('fileName', file.name);
        formData.append('chunkIndex', chunkIndex);
        formData.append('totalChunks', totalChunks);

        try {
          const response = await this.uploadChunk(formData, onProgress);
          if (response.message !== '分块上传成功' && response.message !== '文件上传成功') {
            onError(new Error(response.message));
            return;
          }
          if (chunkIndex === totalChunks - 1) {
            onSuccess(response);
          }
        } catch (error) {
          onError(error);
          return;
        }
      }
    },
    async uploadChunk(formData, onProgress) {
      return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:9090/upload', true);
        xhr.upload.onprogress = event => {
          if (event.lengthComputable) {
            onProgress(event);
          }
        };
        xhr.onload = () => {
          if (xhr.status < 400) {
            resolve(JSON.parse(xhr.responseText));
          } else {
            reject(new Error('上传失败'));
          }
        };
        xhr.onerror = () => {
          reject(new Error('上传失败'));
        };
        xhr.send(formData);
      });
    }
  }
};
</script>

<style>
.upload-demo .el-upload__tip {
  line-height: 1.2;
}
.file-upload-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
h1 {
  text-align: center;
  margin-bottom: 20px;
}
.upload-section {
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ebebeb;
  border-radius: 5px;
  background-color: #f9f9f9;
}
.upload-demo .el-upload__tip {
  line-height: 1.2;
}

.upload-demo .el-button {
  display: block;
  margin: 0 auto 10px auto;
}
</style>