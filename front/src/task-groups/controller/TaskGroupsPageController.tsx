import { Button, Toolbar } from "@mui/material";
import TaskGroupsView from "../view/components/TaskGroupView";
import { Link, Route, Switch, useLocation } from "wouter";
import { createTaskGroup, createTaskInTaskGroup, getAllTaskGroups, getTasksInTaskGroup } from "../fetch";
import NewTaskGroupView from "../view/components/NewTaskGroupView";
import TaskGroupDetails from "../view/components/TaskGroupDetails";
import { TaskGroup } from "../types/TaskGroup";
import { Task } from "../types/Task";
import NewTaskInTaskGroupView from "../view/components/NewTaskInTaskGroupView";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useAllTaskGroupsQueryResult, useGetTaskGroupQueryResult, useNewTaskGroupMutation, useTasksByTaskGroupQueryResult } from "../hooks";
import EditTaskInTaskGroupView from "../view/components/EditTaskInTaskGroupView";

const RenderTaskGroups = () => {
    const allTaskGroupsQr = useAllTaskGroupsQueryResult();
    const tasksByTaskGroupQrFactory = (taskGroup: TaskGroup) => useTasksByTaskGroupQueryResult(taskGroup)

    return (
        <>
            <Toolbar>
                <Button
                    component={Link}
                    href="/new"
                    variant="contained"
                    color="primary"
                >
                    New Task Group
                </Button>
            </Toolbar>
            {allTaskGroupsQr.isLoading ? (
                <div>Loading...</div>
            ) : allTaskGroupsQr.isError ? (
                <div>Error fetching task groups</div>
            ) : !allTaskGroupsQr.isData ? (
                <div>No task groups found</div>
            ) : (
                <TaskGroupsView taskLists={allTaskGroupsQr.data} createTaskQuery={tasksByTaskGroupQrFactory} />
            )}
        </>
    );
}

const RenderNewTaskGroup = () => {
    const mutation = useNewTaskGroupMutation();

    return (
        <NewTaskGroupView mutation={mutation} />
    );
}

const RenderNewTaskInTaskGroup = ({ taskGroupId }: { taskGroupId: number }) => {
    const [_, setLocation] = useLocation();
    const getTaskGroupQueryResult = useGetTaskGroupQueryResult(taskGroupId);
    const mutation = useMutation<void, Error, { taskGroupId: number, task: Task }>({
        mutationFn: createTaskInTaskGroup,
        onSuccess: () => {
            getTaskGroupQueryResult.refetch ? getTaskGroupQueryResult.refetch() : null;
            setLocation("/");
        }
    });

    return (
        <NewTaskInTaskGroupView mutation={mutation} taskGroupId={taskGroupId} />
    );
}

const RenderEditTaskInTaskGroup = ({ taskGroupId, taskId }: { taskGroupId: number, taskId: number }) => {
    const [_, setLocation] = useLocation();

    return (
        <EditTaskInTaskGroupView taskGroupId={taskGroupId} taskId={taskId} />
    );
}

const TaskGroupPageController = () => {
    return (
        <>
            <Switch>
                <Route path="/new">
                    <RenderNewTaskGroup />
                </Route>
                <Route path="/:id">
                    {({ id }) => <TaskGroupDetails id={id} />}
                </Route>
                <Route path="/:id/tasks/new">
                    {({ id }) => <RenderNewTaskInTaskGroup taskGroupId={parseInt(id)} />}
                </Route>
                <Route path="/:groupId/tasks/:id/edit">
                    {({ groupId, id }) => <RenderEditTaskInTaskGroup taskGroupId={parseInt(groupId)} taskId={parseInt(id)} />}
                </Route>
                <Route path="/">
                    <RenderTaskGroups />
                </Route>
            </Switch>
        </>
    );
};

export default TaskGroupPageController;