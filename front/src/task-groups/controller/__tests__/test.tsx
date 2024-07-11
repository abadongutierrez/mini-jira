import { render, renderHook, screen, waitFor } from "@testing-library/react";
import { useAllTaskGroupsQueryResult } from "../../hooks";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { getAllTaskGroups } from "../../fetch";

jest.mock("../../fetch");

const getAllTaskGroupsMocked = jest.mocked(getAllTaskGroups);
getAllTaskGroupsMocked.mockReset();

const TestComponent = () => {
    const allTaskGroupsQr = useAllTaskGroupsQueryResult();
    return (
        <div>
            <div data-testid="loading">{'' + allTaskGroupsQr.isLoading}</div>
            <div data-testid="error">{'' + allTaskGroupsQr.isError}</div>
            <div data-testid="data">{'' + allTaskGroupsQr.isData}</div>
            {allTaskGroupsQr.data && <div data-testid="taskGroups">There is data</div>}
        </div>
    );
}

const Wrapper = ({ children }: { children: React.ReactNode }) => {
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false,
            },
        },
    });
    return (
        <QueryClientProvider client={queryClient}>
            {children}
        </QueryClientProvider>
    );
}

const createWrapper = () => {
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false,
            },
        },
    })
    return ({ children }: { children: React.ReactNode }) => (
        <QueryClientProvider client={queryClient}>
            {children}
        </QueryClientProvider>
    )
}

describe("App", () => {
    describe('with TestComponent', () => {
        afterEach(() => {
            jest.clearAllMocks();
        });

        it("should be loading...", () => {
            render(<Wrapper><TestComponent /></Wrapper>);
            expect(screen.getByTestId("loading").textContent).toBe("true");
            expect(getAllTaskGroups).toHaveBeenCalledTimes(1);
        });

        it("should show data", async () => {
            getAllTaskGroupsMocked.mockResolvedValueOnce([{ id: 1, name: "Task Group 1" }]);
            render(<Wrapper><TestComponent /></Wrapper>);
            // wait for data to show up
            await waitFor(() => expect(screen.getByTestId("taskGroups")).toBeInTheDocument());
            expect(screen.getByTestId("loading").textContent).toBe("false");
            expect(screen.getByTestId("error").textContent).toBe("false");
            expect(screen.getByTestId("data").textContent).toBe("true");
            expect(getAllTaskGroups).toHaveBeenCalledTimes(1);
        });

        it("should fail", async () => {
            getAllTaskGroupsMocked.mockRejectedValueOnce(new Error("Failed to fetch"));
            render(<Wrapper><TestComponent /></Wrapper>);
            // loading is still true, so wait for error to show up
            await waitFor(() => expect(screen.getByTestId("error").textContent).toBe("true"));
            expect(screen.getByTestId("loading").textContent).toBe("false");
            expect(screen.getByTestId("error").textContent).toBe("true");
            expect(screen.getByTestId("data").textContent).toBe("false");
            expect(getAllTaskGroups).toHaveBeenCalledTimes(1);
        });
    });

    describe('with renderHook', () => {
        afterEach(() => {
            jest.clearAllMocks();
        });

        it("should be loading...", async () => {
            const { result } = renderHook(() => useAllTaskGroupsQueryResult(), {
                wrapper: createWrapper()
            });
            expect(result.current.isLoading).toBe(true);
            expect(getAllTaskGroupsMocked).toHaveBeenCalledTimes(1);
        });

        it("should fail", async () => {
            getAllTaskGroupsMocked.mockRejectedValueOnce(new Error("Failed to fetch"));
            const { result } = renderHook(() => useAllTaskGroupsQueryResult(), {
                wrapper: createWrapper()
            });

            await waitFor(() => expect(result.current.isError).toBe(true));

            expect(result.current.data).toBeUndefined();
            expect(result.current.isLoading).toBe(false);
            expect(result.current.isData).toBe(false);
            expect(result.current.isError).toBe(true);
            expect(result.current.error?.message).toBe("Failed to fetch");
        });

        it("should return data with correct length", async () => {
            getAllTaskGroupsMocked.mockResolvedValueOnce([{ id: 1, name: "Task Group 1" }]);
            const { result } = renderHook(() => useAllTaskGroupsQueryResult(), {
                wrapper: createWrapper()
            });

            await waitFor(() => expect(result.current.data).toBeDefined());

            expect(result.current.data).toHaveLength(1);
            expect(getAllTaskGroupsMocked).toHaveBeenCalledTimes(1);
        });
    });


});
