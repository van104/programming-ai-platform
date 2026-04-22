import request from "@/utils/request";

/**
 * 获取用户列表
 */
export function getUserList() {
  return request({
    url: "user/query/all",
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
