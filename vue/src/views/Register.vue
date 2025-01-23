<template>
  <div class="wrapper">
    <div style="margin: 150px auto; background-color: #fff; width: 350px; height: 350px; padding: 20px; border-radius: 10px">
      <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>Register</b></div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input placeholder="Please enter your account" size="medium" prefix-icon="el-icon-user" v-model="user.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="Please enter your password" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input placeholder="Please confirm your password" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.confirmPassword"></el-input>
        </el-form-item>
        <el-form-item style="margin: 5px 0; text-align: right">
          <el-button type="primary" size="small" autocomplete="off" @click="login">Register</el-button>
          <el-button type="warning" size="small" autocomplete="off" @click="$router.push('/login')">Back to Login</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      user: {},
      rules: {
        username: [
          { required: true, message: 'Please enter a username', trigger: 'blur' },
          { min: 3, max: 10, message: 'Length should be between 3 and 5 characters', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter a password', trigger: 'blur' },
          { min: 1, max: 20, message: 'Length should be between 1 and 20 characters', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: 'Please enter a password', trigger: 'blur' },
          { min: 1, max: 20, message: 'Length should be between 1 and 20 characters', trigger: 'blur' }
        ],
      }
    }
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // Form validation passed
          if (this.user.password !== this.user.confirmPassword) {
            this.$message.error("The passwords entered do not match")
            return false
          }
          this.request.post("/user/register", this.user).then(res => {
            if(res.code === '200') {
              this.$message.success("Registration successful")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    }
  }
}
</script>

<style>
  .wrapper {
    height: 100vh;
    background-image: linear-gradient(to bottom right, #FC466B , #3F5EFB);
    overflow: hidden;
  }
</style>
