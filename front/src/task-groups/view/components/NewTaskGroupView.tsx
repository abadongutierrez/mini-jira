import { useState } from 'react';
import { TextField, Button, Grid, Container, Alert } from '@mui/material';
import { TaskGroup } from '../../types/TaskGroup';
import { useLocation } from 'wouter';
import { UseMutationResult } from '@tanstack/react-query';

interface NewTaskGroupViewProps {
    // TODO find a way to decouple this component from useQuery mutations
    mutation: UseMutationResult<void, Error, TaskGroup, unknown>
};

const NewTaskGroupView: React.FC<NewTaskGroupViewProps> = ({ mutation }) => {
    const [name, setName] = useState('');
    const [_, setLocation] = useLocation();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        mutation.mutate({ name } as TaskGroup);
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

export default NewTaskGroupView;