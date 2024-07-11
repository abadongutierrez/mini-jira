import React, { useEffect } from 'react';
import { Accordion, AccordionDetails, AccordionSummary, Typography, Grid, AccordionActions, Button, List, ListItemText, ListItem, Divider, Paper, Avatar, Chip } from '@mui/material';
import { ExpandMore as ExpandMoreIcon, AssignmentOutlined as AssignmentOutlinedIcon } from '@mui/icons-material';
import { TaskGroup } from '../../types/TaskGroup';
import { useLocation } from 'wouter';
import { QueryResult, useGetTaskGroupQueryResult } from '../../hooks';
import { Task } from '../../types/Task';
import styled from '@emotion/styled';
import TaskInGroupView from './TaskInGroupView';

interface TaskGroupsProps {
  taskLists?: TaskGroup[];
  createTaskQuery: (taskGroup: TaskGroup) => QueryResult<Task[]>
}

interface TaskGroupViewProps {
  taskGroup: TaskGroup;
  createTaskQuery: (taskGroup: TaskGroup) => QueryResult<Task[]>
}

// const Item = styled(Paper)(({ theme }) => ({
//   backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
//   ...theme.typography.body2,
//   padding: theme.spacing(1),
//   textAlign: 'left',
//   color: theme.palette.text.secondary,
//   boxShadow: 'none',
// }));

const TaskGroupView: React.FC<TaskGroupViewProps> = ({ taskGroup, createTaskQuery }) => {
  const [_, setLocation] = useLocation();
  const { isLoading, isError, data: tasksInGroupList, error, refetch } = createTaskQuery(taskGroup);
  const getTaskGroupQueryResult = useGetTaskGroupQueryResult(taskGroup.id);

  const onChange = (_event: React.SyntheticEvent, newExpanded: boolean) => {
    if (newExpanded) {
      refetch ? refetch() : null;
    }
  }

  const onTaskDeleted = (task: Task) => {
    refetch ? refetch() : null;
    getTaskGroupQueryResult.refetch ? getTaskGroupQueryResult.refetch() : null;
  }

  return (
    <Accordion onChange={onChange}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1a-content"
        id="panel1a-header"
      >
        <Grid container spacing={1} xs={12}>
          <Grid item container xs={8}>
            <Grid item>
              <Typography variant='subtitle1'>{taskGroup.name}</Typography>
            </Grid>
          </Grid>
          <Grid item container xs={4}>
            <Grid item xs>
            </Grid>
            <Grid item>
              {getTaskGroupQueryResult.isData && <Chip label={getTaskGroupQueryResult.data?.totalEstimation} size="small" />}
              {!getTaskGroupQueryResult.isData && <Chip label={taskGroup.totalEstimation} size="small" />}
              <Button color='info' onClick={(e) => e.preventDefault()}>Start Sprint</Button>
            </Grid>
          </Grid>
        </Grid>
      </AccordionSummary>
      <AccordionDetails>
        {tasksInGroupList ? (
          tasksInGroupList.length === 0 ? (
            <Typography variant='body1'>No tasks in this group</Typography>
          ) : (
            <>
            <Grid container spacing={1}>
              {tasksInGroupList.map((task) =>
                <TaskInGroupView taskGroup={taskGroup} task={task} onTaskDeleted={() => onTaskDeleted(task)}/>)}
            </Grid>
            </>
          )
        ) : isError ? (
          <span>Error: {error?.message}</span>
        ) : isLoading ? (
          <span>Loading...</span>
        ) : (
          <span>Not ready ...</span>
        )}
      </AccordionDetails>
      <AccordionActions>
        <Button color='success' onClick={() => setLocation(`/${taskGroup.id}/tasks/new`)}>+ Task</Button>
      </AccordionActions>
    </Accordion>
  );
}

const TaskGroupsView: React.FC<TaskGroupsProps> = ({ taskLists: taskGroupsList, createTaskQuery }) => {

  return (
    <Grid container spacing={1}>
      {taskGroupsList!.map((taskGroup: TaskGroup) => (
        <Grid item xs={12} key={taskGroup.id}>
          <TaskGroupView taskGroup={taskGroup} createTaskQuery={createTaskQuery} />
        </Grid>
      ))}
    </Grid>
  );
};

export default TaskGroupsView;