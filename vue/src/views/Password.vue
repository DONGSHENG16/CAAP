<template>
  <el-card style="width: 500px;">
    <el-form label-width="120px" size="small" :model="form" :rules="rules" ref="pass">

      <el-form-item label="Old Password" prop="password">
        <el-input v-model="form.password" autocomplete="off" show-password></el-input>
      </el-form-item>
      <el-form-item label="New Password" prop="newPassword">
        <el-input v-model="form.newPassword" autocomplete="off" show-password></el-input>
      </el-form-item>
      <el-form-item label="Confirm New Password" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" autocomplete="off" show-password></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">Confirm</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  name: "Password",
  data() {
    return {
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      rules: {
        password: [
          { required: true, message: 'Please enter the old password', trigger: 'blur' },
          { min: 3, message: 'Length should be at least 3 characters', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: 'Please enter the new password', trigger: 'blur' },
          { min: 3, message: 'Length should be at least 3 characters', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: 'Please confirm the new password', trigger: 'blur' },
          { min: 3, message: 'Length should be at least 3 characters', trigger: 'blur' }
        ],
      }
    }
  },
  created() {
    this.form.username = this.user.username
  },
  methods: {
    save() {
      this.$refs.pass.validate((valid) => {
        if (valid) {
          if (this.form.newPassword !== this.form.confirmPassword) {
            this.$message.error("The two new passwords do not match")
            return false
          }
          this.request.post("/user/password", this.form).then(res => {
            if (res.code === '200') {
              this.$message.success("Successfully changed")
              this.$store.commit("logout")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
  }
}
</script>

<style>
.avatar-uploader {
  text-align: center;
  padding-bottom: 10px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 138px;
  height: 138px;
  line-height: 138px;
  text-align: center;
}
.avatar {
  width: 138px;
  height: 138px;
  display: block;
}
</style>
