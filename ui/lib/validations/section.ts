import * as z from "zod"

export const sectionFormSchema = z.object({
  name: z.string().min(1, "Name is required"),
  geologicalClasses: z.array(z.object({
    id: z.number().optional(),
    name: z.string(),
    code: z.string(),
  })).min(1, "At least one geological class is required"),
})

export type SectionFormValues = z.infer<typeof sectionFormSchema>