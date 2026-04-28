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
/**
 * 反引号``，用于包裹字符串，表示这是一个模板字符串，可以包含变量和表达式
 */