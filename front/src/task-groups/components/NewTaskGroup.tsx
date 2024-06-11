import { useState } from 'react';
import { useMutation } from 'react-query';
import { useLocation } from "wouter";
import { createTaskGroup } from '../fetch';
import TaskGroup from '../types/TaskGroup';
import { TextField, Button, Grid, Container } from '@mui/material';

function NewTaskGroup() {
    const [name, setName] = useState('');
    const [_, setLocation] = useLocation();

    const mutation = useMutation(createTaskGroup, {
        onSuccess: (data: string) => {
            console.log(data);
            setLocation(data); // Ensure data is always a string
        },
    });

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        mutation.mutate({ name } as TaskGroup);
    };

    return (
        <Container maxWidth="sm">
            <form onSubmit={handleSubmit}>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <TextField
                            label="Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            fullWidth
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <Button variant="contained" color="primary" type="submit">
                            Create Task Group
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Container>
    );
}

export default NewTaskGroup;