import { useState } from 'react';
import { useMutation } from 'react-query';
import { useLocation } from "wouter";
import { createTaskList } from '../fetch';
import TaskList from '../types/TaskList';

function NewTaskList() {
    const [name, setName] = useState('');
    const [type, setType] = useState('');
    const [status, setStatus] = useState('');

    const [_, setLocation] = useLocation();

    const mutation = useMutation(createTaskList, {
        onSuccess: (data: string) => {
            console.log(data);
            setLocation(data); // Ensure data is always a string
        },
    });

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        mutation.mutate({ name, type, status } as TaskList);
    };

    return (
        <form onSubmit={handleSubmit}>
            <label>
                Name:
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
            </label>
            <label>
                Type:
                <input type="text" value={type} onChange={(e) => setType(e.target.value)} />
            </label>
            <label>
                Status:
                <input type="text" value={status} onChange={(e) => setStatus(e.target.value)} />
            </label>
            <button type="submit">Create Task List</button>
        </form>
    );
}

export default NewTaskList;