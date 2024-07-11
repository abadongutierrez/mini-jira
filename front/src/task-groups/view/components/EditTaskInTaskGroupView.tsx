import { useState } from 'react';
import { TextField, Button, Grid, Container, Alert } from '@mui/material';
import { Task } from '../../types/Task';
import { useLocation } from 'wouter';
import { UseMutationResult } from '@tanstack/react-query';
import { useEditTaskInGroupMutation, useGetTaskGroupQueryResult, useGetTaskInGroupQueryResult, useTasksByTaskGroupQueryResult } from '../../hooks';

interface EditTaskInTaskGroupViewProps {
    taskGroupId: number;
    taskId: number;
};

const FormEditTask = ({ taskGroupId, task }: { taskGroupId: number, task: Task }) => {
    const [_, setLocation] = useLocation();
    const [name, setName] = useState(task.name);
    const [description, setDescription] = useState(task.description);
    const [estimation, setEstimation] = useState<number | undefined>(task.estimation);
    const mutation = useEditTaskInGroupMutation(() => setLocation("/"));
    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        mutation.mutate({ taskGroupId, task: { ...task, name, description, estimation } });
    };
    return (
        <form onSubmit={handleSubmit}>
            <Grid container spacing={2}>
                {mutation.isError &&
                        <Grid item xs={12}>
                            <Alert severity="error">{mutation.error.message}</Alert>
                        </Grid>
                }
                <Grid item xs={12}>
                    <Grid item xs={12}>
                        <TextField
                            label="Task Group ID"
                            value={taskGroupId}
                            disabled
                            fullWidth
                        />
                    </Grid>
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        label="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        error={mutation.isError}
                        //helperText={mutation.isError ? mutation.error?.message : ''}
                        fullWidth
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        label="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        error={mutation.isError}
                        //helperText={mutation.isError ? mutation.error?.message : ''}
                        fullWidth
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        label="Estimation"
                        value={estimation}
                        onChange={(e) => setEstimation(Number.parseFloat(e.target.value))}
                        error={mutation.isError}
                        //helperText={mutation.isError ? mutation.error?.message : ''}
                        fullWidth
                    />
                </Grid>
                <Grid item xs={12}>
                    <Button variant="contained" color="success" type="submit" disabled={mutation.isPending}>
                        Submit
                    </Button>
                    <Button variant="contained" color="error" type="reset" onClick={() => setLocation("/")}>
                        Cancel
                    </Button>
                </Grid>
            </Grid>
        </form>
    );
};

const EditTaskInTaskGroupView: React.FC<EditTaskInTaskGroupViewProps> = ({ taskGroupId, taskId }) => {

    const taskInGroupQueryResult = useGetTaskInGroupQueryResult(taskGroupId, taskId);

    return (
        <Container maxWidth="sm">
            {taskInGroupQueryResult.isLoading ?
                (<div>Loading...</div>)
                : taskInGroupQueryResult.isError ?
                    (<div>Error</div>)
                    : !taskInGroupQueryResult.isData ?
                        (<div>No data</div>)
                        : <FormEditTask task={taskInGroupQueryResult.data!} taskGroupId={taskGroupId} />
            }
        </Container>
    );
}

export default EditTaskInTaskGroupView;