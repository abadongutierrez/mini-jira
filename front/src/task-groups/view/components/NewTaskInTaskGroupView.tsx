import { useState } from 'react';
import { TextField, Button, Grid, Container, Alert } from '@mui/material';
import { Task } from '../../types/Task';
import { useLocation } from 'wouter';
import { UseMutationResult } from '@tanstack/react-query';

interface NewTaskInTaskGroupViewProps {
    // TODO find a way to decouple this component from useQuery mutations
    mutation: UseMutationResult<void, Error, { taskGroupId: number, task: Task }, unknown>;
    taskGroupId: number;
};

const NewTaskInTaskGroupView: React.FC<NewTaskInTaskGroupViewProps> = ({ mutation, taskGroupId }) => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [estimation, setEstimation] = useState<number | undefined>(undefined);
    const [_, setLocation] = useLocation();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        mutation.mutate({
            taskGroupId,
            task: { name, description, estimation } as Task
        });
    };

    return (
        <Container maxWidth="sm"> 
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
        </Container>
    );
}

export default NewTaskInTaskGroupView;