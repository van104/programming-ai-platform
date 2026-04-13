package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.Record;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lrm
 * @since 2026-03-16
 */
@RestController
@RequestMapping("/record")
public class RecordController {

    private final com.lrm.aiplatform.service.IRecordService recordService;

    public RecordController(com.lrm.aiplatform.service.IRecordService recordService) {
        this.recordService = recordService;
    }

    //新增记录
    @GetMapping("/add")
    public Result<String> addRecord() {
        Record record = new Record();
        record.setUserId(1L);
        record.setCodeContent("测试文章");
        record.setResult("测试成功！");
        record.setCreateTime(LocalDateTime.now());
        recordService.save(record);
        return Result.success("记录添加成功", null);
    }

    //全局删除
    @GetMapping("/delete/")
    public Result<String> deleteAllRecords() {
        recordService.remove(null);
        return Result.success("所有记录删除成功", null);
    }

    //删除记录，根据id删除
    @GetMapping("/delete/{id}")
    public Result<String> deleteRecord(@PathVariable Long id) {
        recordService.removeById(id);
        return Result.success("记录删除成功", null);
    }
 /*   //访问如下方法的URL是传统风格的url：
//根目录/user/delete1?id=1
    @GetMapping("/user/delete1")
    public Result<String> delete1(@RequestParam  long id) {
        userService.removeById(id);
        return Result.success("用户删除成功", null);
    }
*/

    //更新记录根据id
    @GetMapping("/update/{id}")
    public Result<String> updateRecord(@PathVariable Long id) {
        Record record = recordService.getById(id);
        record.setCodeContent("这是一条修改过的测试文章");
        record.setResult("测试成功！");
        record.setCreateTime(LocalDateTime.now());
        recordService.updateById(record);
        return Result.success("记录更新成功", null);
    }

    //查询记录，根据id查询
    @GetMapping("/query/{id}")
    public Result<Record> queryRecord(@PathVariable Long id) {
        Record record = recordService.getById(id);
        return Result.success("记录查询成功", record);
    }

    //查询所有记录
    @GetMapping("/query/all")
    public Result<List<Record>> queryAllRecords() {
        List<Record> recordList = recordService.list();
        return Result.success("所有记录查询成功", recordList);
    }
}
