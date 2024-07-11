export interface TaskNoId {
    name: string;
    description: string;
    estimation?: number;
}


export interface Task extends TaskNoId {
    id: number;
}