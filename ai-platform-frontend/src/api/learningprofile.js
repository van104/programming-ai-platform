import request from "@/utils/request";
/**
 * 获取ai使用次数，代码提交次数，活跃度
 */
export function getUserData(Id) {
  return request({
    url: `/profile/full/${Id}`,
    method: "get",
  });
}
