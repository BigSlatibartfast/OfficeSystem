package com.atguigu.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessTest {

    //注入RepositoryService
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    //单个流程实例挂起
    @Test
    public void SingleSuspendProcessInstance() {
        String processInstanceId = "a6a30acd-cbb7-11ed-b402-00ff1ab64402";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        boolean suspended = processInstance.isSuspended();

        if (suspended) {
            //激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println(processInstanceId + "激活");
        } else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println(processInstanceId + "挂起");
        }
    }


    //全部流程实例挂起
    @Test
    public void suspendProcessInstanceAll() {
        //1.获取流程定义的对象
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("qingjia").singleResult();

        //2.调用流程定义对象的方法判断当前状态：挂起  激活
        boolean suspended = qingjia.isSuspended();

        //3.判断如果挂起，实现激活
        if (suspended) {
            //第一个参数：流程定义id
            //第二个参数：是否激活 true
            //第三个参数  时间点
            repositoryService.activateProcessDefinitionById(qingjia.getId(), true, null);

            System.out.println(qingjia.getId() + "激活了");
        } else {
            //如果激活状态，实现挂起
            repositoryService.suspendProcessDefinitionById(qingjia.getId(), true, null);

            System.out.println(qingjia.getId() + "挂起");
        }

    }


    //创建流程实例，指定BusinessKey
    @Test
    public void startUpProcessAddBusinessKey() {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("qingjia", "1001");

        System.out.println(instance.getBusinessKey());
        System.out.println(instance.getId());
    }


    /**
     * 删除流程定义
     */
    @Test
    public void deleteDeployment() {
        //部署id
        String deploymentId = "7132603c-cbb1-11ed-8eaf-00ff1ab64402";
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        // repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式
        repositoryService.deleteDeployment(deploymentId, true);
    }


    //查询已经处理的任务
    @Test
    public void findCompleteTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("zhangsan")
                .finished().list();

        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("实例id：" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id：" + historicTaskInstance.getId());
            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
            System.out.println("任务名称：" + historicTaskInstance.getName());
        }
    }

    //处理当前任务
    @Test
    public void completeTAsk() {
        //查询负责人需要处理的任务，返回一条
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .singleResult();
        //完成任务,参数是任务id
        taskService.complete(task.getId());
    }


    //查询个人代办任务--zhangsan
    @Test
    public void findTaskList() {

        String assign = "zhangsan";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();

        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }


    //启动流程实例
    @Test
    public void startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("流程活动id：" + processInstance.getActivityId());
    }


    //单个文件部署
    @Test
    public void deployProcess() {
        //流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();

        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

    }

    //删除流程实例
    @Test
    public void deleteInstance() {
        String processInstanceId = "712a49e8-cbb1-11ed-8eaf-00ff1ab64402";
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (pi == null) {
            //该流程实例已经完成了historyService.deleteHistoricProcessInstance(processInstanceId);
        } else {
            //该流程实例未结束的
            runtimeService.deleteProcessInstance(processInstanceId, "");
            historyService.deleteHistoricProcessInstance(processInstanceId);//(顺序不能换)
        }

    }
}
