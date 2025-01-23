<template>
  <div class="wrapper">
    <div style="position: relative; top: 30%; left: 1%;text-align: center;color: aliceblue; font-size: 35px"><b>CAAP</b></div>
    <div
        style="margin: 300px auto; background-color: #277081;opacity:0.8; width: 350px; height: 280px; padding: 20px; border-radius: 10px">
      <div style="margin: 20px 0; text-align: center;color: white; font-size: 24px"><b>Login</b></div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input size="medium" prefix-icon="el-icon-user" v-model="user.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium" prefix-icon="el-icon-lock" show-password
                    v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="primary" size="small" autocomplete="off" @click="login">Login</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {setRoutes} from "@/router";

export default {
  name: "Login",
  data() {
    return {
      user: {},
      rules: {
        username: [
          {required: true, message: 'Please enter your username', trigger: 'blur'},
          {min: 3, max: 10, message: 'Length must be between 3 and 10 characters', trigger: 'blur'}
        ],
        password: [
          {required: true, message: 'Please enter your password', trigger: 'blur'},
          {min: 1, max: 20, message: 'Length must be between 1 and 20 characters', trigger: 'blur'}
        ],
      }
    }
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // Form validation passed
          this.request.post("/user/login", this.user).then(res => {
            if (res.code === '200') {
              localStorage.setItem("user", JSON.stringify(res.data))  // Store user information in browser
              localStorage.setItem("menus", JSON.stringify(res.data.menus))  // Store user menus in browser
              // Dynamically set routes for the current user
              setRoutes()
              this.$message.success("Login successful")

              if (res.data.role === 'ROLE_STUDENT') {
                this.$router.push("/home")
              } else {
                this.$router.push("/")
              }
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

<style scoped>
.wrapper {
  height: 100vh;
  background-image: url(../assets/genBG.gif);
  overflow: hidden;
}
</style>
