import { Button, Toolbar } from "@mui/material";
import TaskGroupsView from "../view/components/TaskGroups";
import { Link, Route, Switch, useLocation } from "wouter";
import { createTaskGroup, createTaskInTaskGroup, getAllTaskGroups, getTasksInTaskGroup } from "../fetch";
import NewTaskGroupView from "../view/components/NewTaskGroup";
import TaskGroupDetails from "../view/components/TaskGroupDetails";
import { TaskGroup } from "../types/TaskGroup";
import { Task } from "../types/Task";
import NewTaskInTaskGroupView from "../view/components/NewTaskInTaskGroup";
import { useMutation, useQuery } from "@tanstack/react-query";

const RenderTaskGroups = () => {
    const { data: taskLists, isLoading, isError } = useQuery({ queryKey: ['taskGroups'], queryFn: getAllTaskGroups });
    const createTaskQuery = (taskGroupId: number) => 
        useQuery({
            queryKey: ['tasksInGroup', `${taskGroupId}`],
            queryFn: () => getTasksInTaskGroup(taskGroupId),
            enabled: false // don't fetch by default only when refetch() is called
        });

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
            {isLoading && <div>Loading...</div>}
            {isError && <div>Error fetching task groups</div>}
            {!taskLists && <div>No task groups found</div>}
            {taskLists && <TaskGroupsView taskLists={taskLists} createTaskQuery={createTaskQuery} />}
        </>
    );
}

const RenderNewTaskGroup = () => {
    const [_, setLocation] = useLocation();
    const mutation = useMutation<void, Error, TaskGroup>({
        mutationFn: createTaskGroup,
        onSuccess: () => {
            setLocation("/");
        }
    });

    return (
        <NewTaskGroupView mutation={mutation} />
    );
}

const RenderNewTaskInTaskGroup = ({ taskGroupId }: { taskGroupId: string }) => {
    const [_, setLocation] = useLocation();
    const mutation = useMutation<void, Error, { taskGroupId: string, task: Task}>({
        mutationFn: createTaskInTaskGroup,
        onSuccess: () => {
            setLocation("/");
        }
    });

    return (
        <NewTaskInTaskGroupView mutation={mutation} taskGroupId={taskGroupId} />
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
                    {({ id }) => <RenderNewTaskInTaskGroup taskGroupId={id} />}
                </Route>
                <Route path="/">
                    <RenderTaskGroups />
                </Route>
            </Switch>
        </>
    );
};

export default TaskGroupPageController;