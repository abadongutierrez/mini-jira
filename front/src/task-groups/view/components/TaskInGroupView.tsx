import { Avatar, Button, Chip, Grid, IconButton, Menu, MenuItem, Paper, Typography } from "@mui/material";
import { Task } from "../../types/Task";
import AssignmentOutlinedIcon from "@mui/icons-material/AssignmentOutlined"; // Import the AssignmentOutlinedIcon component
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { TaskGroup } from "../../types/TaskGroup";
import { useDeleteTaskInGroupMutaion } from "../../hooks";
import { useState } from "react";
import { useLocation } from "wouter";

interface TaskInGroupViewProps {
    task: Task;
    taskGroup: TaskGroup;
    onTaskDeleted: () => void;
}

const TaskInGroupView: React.FC<TaskInGroupViewProps> = ({ task, taskGroup, onTaskDeleted }) => {
    const [_, setLocation] = useLocation();
    const deleteTaskMutation = useDeleteTaskInGroupMutaion(onTaskDeleted);
    const [deleteIconAnchorEl, setDeleteIconAnchorEl] = useState<null | HTMLElement>(null);
    const openDelete = Boolean(deleteIconAnchorEl);
    const handleDeleteClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setDeleteIconAnchorEl(event.currentTarget);
    };
    const handleEditClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setLocation(`/${taskGroup.id}/tasks/${task.id}/edit`);
    }
    const handleClose = () => {
        setDeleteIconAnchorEl(null);
    };


    const handleDelete = () => {
        handleClose();
        deleteTaskMutation.mutate({ taskGroupId: taskGroup.id, taskId: task.id });
    }

    return (
        <Grid container item xs={12} key={task.id} spacing={1}>
            <Grid item xs={12}>
                <Paper sx={{ borderRadius: 0, paddingLeft: 1, paddingRight: 1, backgroundColor: '#f5f5f5' }}>
                    <Grid container item xs={12} key={task.id} spacing={1}>
                        <Grid container item xs={8} key={task.id} spacing={1}>
                            <Grid item>
                                <AssignmentOutlinedIcon />
                            </Grid>
                            <Grid item>
                                <Typography variant="subtitle1">{task.name}</Typography>
                            </Grid>
                            <Grid item>
                                <Typography variant="subtitle2">{task.description}</Typography>
                            </Grid>
                        </Grid>
                        <Grid container item xs={4} key={task.id} spacing={1}>
                            <Grid item xs>
                            </Grid>
                            <Grid item>
                                <Chip label={task.estimation} size="small" />
                            </Grid>
                            <Grid item>
                                <Chip label="To Do" size="small" />
                            </Grid>
                            <Grid item>
                                <Avatar sx={{ width: 24, height: 24 }} />
                            </Grid>
                            <Grid item>
                                <IconButton color="primary"
                                    onClick={handleEditClick}>
                                    <EditIcon />
                                </IconButton>
                            </Grid>
                            <Grid item>
                                <IconButton color="error"
                                    aria-controls={openDelete ? 'basic-menu' : undefined}
                                    aria-haspopup="true"
                                    aria-expanded={openDelete ? 'true' : undefined}
                                    onClick={handleDeleteClick}>
                                    <DeleteIcon />
                                </IconButton>
                                <Menu
                                    anchorEl={deleteIconAnchorEl}
                                    open={openDelete}
                                    onClose={handleClose}
                                    MenuListProps={{
                                        'aria-labelledby': 'basic-button',
                                    }}
                                >
                                    <MenuItem onClick={handleDelete}>Yes</MenuItem>
                                </Menu>
                            </Grid>
                        </Grid>
                    </Grid>
                </Paper>
            </Grid>
        </Grid>
    );
};

export default TaskInGroupView;