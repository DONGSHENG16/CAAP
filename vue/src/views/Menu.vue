<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="Please enter name" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">Search</el-button>
      <el-button type="warning" @click="reset">Reset</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd(null)">Add New <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='Confirm'
          cancel-button-text='I will think again'
          icon="el-icon-info"
          icon-color="red"
          title="Are you sure you want to delete these data in bulk?"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">Bulk Delete <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
              row-key="id" default-expand-all @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="Name"></el-table-column>
      <el-table-column prop="path" label="Path"></el-table-column>
      <el-table-column prop="pagePath" label="Page Path"></el-table-column>
      <el-table-column label="Icon" class-name="fontSize18" align="center" label-class-name="fontSize12">
        <template slot-scope="scope">
          <span :class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="Description"></el-table-column>
      <el-table-column prop="sortNum" label="Order"></el-table-column>
      <el-table-column label="Actions" width="300" align="center">
        <template slot-scope="scope">
          <el-button type="primary" @click="handleAdd(scope.row.id)" v-if="!scope.row.pid && !scope.row.path">Add Submenu <i class="el-icon-plus"></i></el-button>
          <el-button type="success" @click="handleEdit(scope.row)">Edit <i class="el-icon-edit"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='Confirm'
              cancel-button-text='I will think again'
              icon="el-icon-info"
              icon-color="red"
              title="Are you sure you want to delete?"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">Delete <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="Menu Information" :visible.sync="dialogFormVisible" width="30%" >
      <el-form label-width="80px" size="small">
        <el-form-item label="Name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Path">
          <el-input v-model="form.path" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Page Path">
          <el-input v-model="form.pagePath" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Icon">
          <el-select clearable v-model="form.icon" placeholder="Please select" style="width: 100%">
            <el-option v-for="item in options" :key="item.name" :label="item.name" :value="item.value">
              <i :class="item.value" /> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Order">
          <el-input v-model="form.sortNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {serverIp} from "../../public/config";

export default {
  name: "Menu",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      options: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/menu", {
        params: {
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data
      })

      // Request icon data
      this.request.get("/menu/icons").then(res => {
        this.options = res.data
      })
    },
    save() {
      this.request.post("/menu", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("Saved successfully")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("Save failed")
        }
      })
    },
    handleAdd(pid) {
      this.dialogFormVisible = true
      this.form = {}
      if (pid) {
        this.form.pid = pid
      }
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/menu/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("Deleted successfully")
          this.load()
        } else {
          this.$message.error("Delete failed")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/menu/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("Batch delete successful")
          this.load()
        } else {
          this.$message.error("Batch delete failed")
        }
      })
    }
  }
}
</script>

<style scoped>
/* Your styles here */
</style>
