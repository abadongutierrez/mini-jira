import { useMutation, useQuery } from "@tanstack/react-query";
import { TaskGroup } from "./types/TaskGroup";
import { createTaskGroup, deleteTaskInGroup, editTaskInTaskGroup, getAllTaskGroups, getTaskGroup, getTaskInTaskGroup, getTasksInTaskGroup } from "./fetch";
import { Task } from "./types/Task";
import { useLocation } from "wouter";

export interface QueryResult<T> {
    data?: T;
    isLoading: boolean;
    isError: boolean;
    error?: Error;
    isData: boolean;
    refetch?: () => void;
}

export const useAllTaskGroupsQueryResult = () => {
    const { data: taskLists, isLoading, isError, error, refetch } =
        useQuery({ queryKey: ['taskGroups'], queryFn: getAllTaskGroups });
    return {
        data: taskLists,
        error,
        isLoading,
        isError,
        isData: !!taskLists && taskLists.length > 0,
        refetch: () => refetch()
    } as QueryResult<TaskGroup[]>;
}

export const useGetTaskGroupQueryResult = (id: number) => {
    const { data: taskGroup, isLoading, isError, error, refetch } =
        useQuery({
            queryKey: ['taskGroup', id],
            queryFn: () => getTaskGroup(id),
            enabled: false
        });
    return {
        data: taskGroup,
        error,
        isLoading,
        isError,
        isData: !!taskGroup,
        refetch: () => refetch()
    } as QueryResult<TaskGroup>;
}

export const useTasksByTaskGroupQueryResult = (taskGroup: TaskGroup) => {
    const { data: tasks, isLoading, isError, error, refetch } = useQuery({
        queryKey: ['tasksInGroup', `${taskGroup.id}`],
        queryFn: () => getTasksInTaskGroup(taskGroup.id),
        enabled: false // don't fetch by default only when refetch() is called
    });
    return {
        data: tasks ? tasks.tasks : [],
        error,
        isLoading,
        isError,
        isData: !!tasks && tasks.tasks.length > 0,
        refetch: () => refetch()
    } as QueryResult<Task[]>;
}

export const useGetTaskInGroupQueryResult = (groupId: number, taskId: number) => {
    const { data: task, isLoading, isError, error, refetch } =
        useQuery({
            queryKey: ['taskInGroup', groupId, taskId],
            queryFn: () => getTaskInTaskGroup({ taskGroupId: groupId, taskId }),
        });
    return {
        data: task,
        error,
        isLoading,
        isError,
        isData: !!task,
        refetch: () => refetch()
    } as QueryResult<Task>;
}

export const useNewTaskGroupMutation = () => {
    const [_, setLocation] = useLocation();
    const mutation = useMutation<void, Error, TaskGroup>({
        mutationFn: createTaskGroup,
        onSuccess: () => {
            setLocation("/");
        }
    });
    return mutation;
}

export const useDeleteTaskInGroupMutaion = (success: () => void) => {
    const mutation = useMutation<void, Error, { taskGroupId: number, taskId: number }>({
        mutationFn: deleteTaskInGroup,
        onSuccess: success
    });
    return mutation;
}

export const useEditTaskInGroupMutation = (success: () => void) => {
    const mutation = useMutation<void, Error, { taskGroupId: number, task: Task }>({
        mutationFn: editTaskInTaskGroup,
        onSuccess: success
    });
    return mutation;
}