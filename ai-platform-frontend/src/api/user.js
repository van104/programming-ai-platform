import request from "@/utils/request";

/**
 * 获取用户列表 (第4天版)
 */
export function getUserList() {
  return request({
    url: "/user/list",
    method: "get",
  });
}

/**
 * 根据id删除用户
 */
export function delUserById(id) {
  return request({
    url: `user/delete/${id}`,
    method: "delete",
  });
}
