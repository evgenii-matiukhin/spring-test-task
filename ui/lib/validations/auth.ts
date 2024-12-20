import * as z from "zod"

export const loginFormSchema = z.object({
  username: z.string().min(2, {
    message: "Username must be at least 2 characters.",
  }),
  password: z.string().min(2, {
    message: "Password must be at least 6 characters.",
  }),
})

export type LoginFormValues = z.infer<typeof loginFormSchema>