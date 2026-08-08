# Cutoff Data Sources

The starter CSV is intentionally small and interview-friendly. It is not a complete official database.

## Normalization Rules

- JoSAA `OPEN` is imported as `GENERAL`.
- JoSAA `OBC-NCL` is imported as `OBC`.
- JoSAA `GEN-EWS` or `EWS` is imported as `EWS`.
- JoSAA `AI` and `OS` quotas are imported as `ALL_INDIA`.
- JoSAA `HS` quota is imported as `HOME_STATE`.
- `Gender-Neutral` is imported as `GENDER_NEUTRAL`.
- `Female-only` is imported as `FEMALE`.

## Source Links Used For Starter Rows

- JoSAA official opening/closing rank portal: https://josaa.admissions.nic.in/applicant/SeatAllotmentResult/CurrentORCR.aspx
- NIT Warangal JoSAA 2023 and 2024 rows: https://engineering.careers360.com/articles/jee-main-cutoff-for-nit-warangal
- NIT Warangal JoSAA 2025 rows: https://www.collegedekho.com/articles/nit-warangal-jee-main-cutoff-rank/ and https://blog.infinitylearn.com/nit-warangal-cutoff
- IIIT Surat JoSAA 2024 Round 5 PDF mirror with official JoSAA table format: https://static.collegedekho.com/media/uploads/2024/07/17/josaa-round-5-iiit-surat-cutoff-2024.pdf
- MCC official counselling website for NEET UG allotment data: https://mcc.nic.in
- AIIMS Delhi and AIIMS Jodhpur NEET UG 2023 final cutoff summary: https://www.collegedekho.com/articles/neet-aiims-cutoff/
- AIIMS Delhi NEET UG 2024 category-wise Round 1 summary: https://www.shiksha.com/articles/aiims-delhi-cutoff-2024-check-neet-ug-category-wise-cutoff-ranks-blogId-187556
- AIIMS Delhi NEET UG 2025 category-wise Round 1 summary: https://www.shiksha.com/articles/aiims-delhi-cutoff-2025-check-neet-ug-category-wise-cutoff-ranks-blogId-187556
- AIIMS MBBS 2024 round-wise cutoff PDF mirror: https://neetugguidance.in/upload_store/89434AIIMS%20MBBS%20CUTOFF%202024.pdf
- JNTUHCE Hyderabad TG EAPCET 2024 cutoff summary: https://www.collegepravesh.com/cutoff/jntuhce-hyderabad-cutoff-2024/
- JNTUA CEA Anantapur AP EAPCET 2024 cutoff summary: https://www.collegepravesh.com/cutoff/jntuacea-anantapur-cutoff-2024/

When a public page gives only a final cutoff rank and not an opening rank, the starter CSV sets `opening_rank` equal to `closing_rank`. The prediction algorithm only depends on closing rank, so those rows remain usable.

For production use, replace `sample-cutoffs.csv` with full official round-wise exports from counselling authorities and keep this file updated with source/date notes.
