export default {
  testEnvironment: "jsdom",
  transform: {
    "^.+\\.tsx?$": "ts-jest"
  },
  setupFilesAfterEnv: ["@testing-library/jest-dom"]
}