<template>
  <div style="color: #666;font-size: 14px;">
    <div style="padding-bottom: 20px">
      <b>Welcome！{{ user.nickname }}</b>
    </div>
    <div style="height: 1px; background: #ddd; margin: 20px 0"/>   
      <h1>CAAP</h1>
   </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  }
}
</script>
